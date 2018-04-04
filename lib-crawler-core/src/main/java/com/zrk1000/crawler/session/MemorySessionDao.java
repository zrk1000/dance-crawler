package com.zrk1000.crawler.session;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * session持久化内存实现
 * User: zhouRongKang
 * Date: 2017/8/21
 * Time: 13:40
 */
public class MemorySessionDao implements SessionDao {

    private ConcurrentMap<String, Session> sessions;

    public MemorySessionDao() {
        this.sessions = new ConcurrentHashMap();
    }

    @Override
    public String create(Session session) {
        if (session == null) {
            throw new NullPointerException("session argument cannot be null.");
        }
        storeSession(session.getId(),session);
        return session.getId();
    }

    @Override
    public Session readSession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void update(Session session) {
        storeSession(session.getId(), session);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            throw new NullPointerException("session argument cannot be null.");
        }
        String id = session.getId();
        if (id != null) {
            sessions.remove(id);
        }

    }

    @Override
    public Collection<Session> getActiveSessions() {
        return sessions.values();
    }

    protected Session storeSession(String id, Session session) {
        if (id == null) {
            throw new NullPointerException("id argument cannot be null.");
        }
        return sessions.putIfAbsent(id, session);
    }
}

    