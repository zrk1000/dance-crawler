package com.zrk1000.crawler.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * session过期校验任务
 * Created by rongkang on 2017-09-03.
 */
public class SimpleSessionValidationScheduler implements SessionValidationScheduler ,Runnable{

    private static Logger logger = LoggerFactory.getLogger(SimpleSessionValidationScheduler.class);

    private long interval = SimpleSessionManager.DEFAULT_SESSION_TIMEOUT;

    private String threadNamePrefix = "SessionValidationThread-";
    private ScheduledExecutorService executorService;

    private SessionDao sessionDao;

    private boolean enable = false;

    public SimpleSessionValidationScheduler(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public void validateSessions() {
        if(!enable){
            this.executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName(threadNamePrefix + count.getAndIncrement());
                    return thread;
                }
            });
            this.executorService.scheduleAtFixedRate(this, interval, interval, TimeUnit.MILLISECONDS);
        }
        this.enable = true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    public SessionDao getSessionDao() {
        return sessionDao;
    }

    public void setSessionDao(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing session validation...");
        }
        long startTime = System.currentTimeMillis();
        Collection<Session> activeSessions = sessionDao.getActiveSessions();
        for (Session session:activeSessions) {
            if(isTimeout(session)){
                sessionDao.delete(session);
            }
        }
        long stopTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Session validation completed successfully in " + (stopTime - startTime) + " milliseconds.");
        }
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


}
