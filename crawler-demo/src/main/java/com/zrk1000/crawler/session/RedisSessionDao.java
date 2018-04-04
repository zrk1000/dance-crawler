package com.zrk1000.crawler.session;

import com.zrk1000.crawler.utils.SerializeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * session持久层 redis实现
 * User: zhouRongKang
 * Date: 2017/8/21
 * Time: 13:40
 */
public class RedisSessionDao implements SessionDao {

    private static String KEY_SET = "crawler_session_id_key_set";
    private static String KEY_PREFIX = "crawler_session_id_";

    private RedisTemplate redisTemplate;
    private ValueOperations<String, String> vOps;
    private SetOperations<String,String> keySet;

    public RedisSessionDao(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.vOps = redisTemplate.opsForValue();
        this.keySet = redisTemplate.opsForSet();
    }

    @Override
    public String create(Session session) {
        if (session == null){
            throw new NullPointerException("session argument cannot be null.");
        }
        storeSession(session.getId(),session);
        return session.getId();
    }

    @Override
    public Session readSession(String sessionId) {
        String storeId = KEY_PREFIX + sessionId;
        return str2Session(vOps.get(storeId));
    }

    @Override
    public void update(Session session) {
        storeSession(session.getId(), session);
    }

    @Override
    public void delete(Session session) {
        if (session == null){
            throw new NullPointerException("session argument cannot be null.");
        }
        String sessionId = session.getId();
        if (sessionId != null){
            String storeId = KEY_PREFIX + sessionId;
            redisTemplate.delete(storeId);
            keySet.remove(KEY_SET,storeId);
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<String> members = keySet.members(KEY_SET);
        List<Session> sessions = new ArrayList();
        if(members!=null){
            List<String> jsonStrs = vOps.multiGet(members);
            for (String jsonStr:jsonStrs) {
                Session session = str2Session(jsonStr);
                if(session != null){
                    sessions.add( session );
                }
            }
        }
        return sessions;
    }

    protected Session storeSession(String sessionId, Session session) {
        if (sessionId == null) {
            throw new NullPointerException("id argument cannot be null.");
        }
        String storeId = KEY_PREFIX + sessionId;
        vOps.set(storeId, session2Str(session),session.getTimeout(), TimeUnit.MILLISECONDS);
        keySet.add(KEY_SET,storeId);
        return str2Session(vOps.get(storeId));
    }

    private String session2Str(Session session){
        if(session == null){
            return null;
        }
        return SerializeUtils.serialize(session);
    }

    private Session str2Session(String sessionStr){
        if(sessionStr == null ){
            return null;
        }
        return SerializeUtils.deserialize(sessionStr,ProxiedSession.class);
    }
}

    