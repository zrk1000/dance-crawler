package com.zrk1000.crawler.session;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * session管理器
 * Created with IntelliJ IDEA.
 * User: zhouRongKang
 * Date: 2017/8/21
 * Time: 13:26
 */
public class SimpleSessionManager implements SessionManager {

    public static long DEFAULT_SESSION_TIMEOUT = 1 * 60 * 1000;

    /**
     * 启用session过期定时扫码
     */
    private boolean enableVlidationScheduler = true;

    private SessionDao sessionDao;

    private SimpleSessionValidationScheduler validationScheduler;

    private Long timout ;

    public SimpleSessionManager() {
        this.sessionDao = new MemorySessionDao();
        this.timout = DEFAULT_SESSION_TIMEOUT;
        initValidationScheduler();
    }

    private void initValidationScheduler(){
        this.validationScheduler = new SimpleSessionValidationScheduler(sessionDao);
    }

    @Override
    public Session getSession(String sessionId) {
        if(enableVlidationScheduler && !validationScheduler.isEnabled()){
            validationScheduler.validateSessions();
        }
        Session session = null;
        if(sessionId==null){
            sessionId =  UUID.randomUUID().toString().replaceAll("-", "");
        }else {
            session = sessionDao.readSession(sessionId);
            if(session!=null&&isTimeout(session)){
                sessionDao.delete(session);
                session = null;
            }
        }
        if(session==null){
            SimpleSession simpleSession = new SimpleSession();
            simpleSession.setSessionId(sessionId);
            simpleSession.setTimeout(timout);
            simpleSession.touch();
            session = simpleSession;
            sessionDao.create(session);
        }
        session.touch();
        return new ProxiedSession(session,sessionDao);
    }

    public SessionDao getSessionDao() {
        return sessionDao;
    }

    public void setSessionDao(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
        initValidationScheduler();
    }

    public Long getTimout() {
        return timout;
    }

    public void setTimout(Long timout) {
        this.timout = timout;
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return sessionDao.getActiveSessions();
    }

    public boolean isTimeout(Session session){
        Date lastAccessTime = session.getLastAccessTime();
        long timeout = session.getTimeout();
        if(timeout > 0){
            long expireTimeMillis = System.currentTimeMillis() - timeout;
            Date expireTime = new Date(expireTimeMillis);
            return lastAccessTime.before(expireTime);
        }
        return false;
    }

    public boolean isEnableVlidationScheduler() {
        return enableVlidationScheduler;
    }

    public void setEnableVlidationScheduler(boolean enableVlidationScheduler) {
        this.enableVlidationScheduler = enableVlidationScheduler;
    }
}

    