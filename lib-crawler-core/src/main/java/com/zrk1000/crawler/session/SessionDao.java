package com.zrk1000.crawler.session;

import java.util.Collection;

/**
 * session持久化接口
 * User: zhouRongKang
 * Date: 2017/8/21
 * Time: 13:30
 */
public interface SessionDao {

    String create(Session session);

    Session readSession(String sessionId);

    void update(Session session);

    void delete(Session session);

    Collection<Session> getActiveSessions();


}
