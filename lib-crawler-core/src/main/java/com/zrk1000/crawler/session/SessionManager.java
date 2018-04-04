package com.zrk1000.crawler.session;

import java.util.Collection;

/**
 * session管理器接口
 * Created by rongkang on 2017-07-11.
 */
public interface SessionManager {


    Session getSession(String sessionId);

    Collection<Session> getActiveSessions();

}
