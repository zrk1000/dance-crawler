package com.zrk1000.crawler.session;

/**
 * Created by rongkang on 2017-09-03.
 */
public interface SessionValidationScheduler {

    void validateSessions();

    boolean isEnabled();

}
