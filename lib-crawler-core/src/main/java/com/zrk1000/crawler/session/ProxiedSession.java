package com.zrk1000.crawler.session;

import com.zrk1000.crawler.visitor.http.HttpCookie;
import com.zrk1000.crawler.visitor.http.Proxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 代理session 支持即时同步session
 * User: zhouRongKang
 * Date: 2017/8/23
 * Time: 14:08
 */
public class ProxiedSession implements Session {

    private Session session;
    private SessionDao sessionDao;

    public ProxiedSession(Session session, SessionDao sessionDao) {
        this.session = session;
        this.sessionDao = sessionDao;
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public Date getStartTimestamp() {
        session = sessionDao.readSession(session.getId());
        if(session == null){
            return null;
        }
        return session.getStartTimestamp();
    }

    @Override
    public Date getLastAccessTime() {
        session = sessionDao.readSession(session.getId());
        if(session == null){
            return null;
        }
        return session.getLastAccessTime();
    }

    @Override
    public long getTimeout() {
        session = sessionDao.readSession(session.getId());
        return session.getTimeout();
    }

    @Override
    public void setTimeout(long maxIdleTimeInMillis) {
        session = sessionDao.readSession(session.getId());
        session.setTimeout(maxIdleTimeInMillis);
        sessionDao.update(session);
    }

    @Override
    public void setAttribute(String key, Serializable value) {
        session = sessionDao.readSession(session.getId());
        session.setAttribute(key, value);
        sessionDao.update(session);
    }

    @Override
    public Serializable getAttribute(String key) {
        session = sessionDao.readSession(session.getId());
        if(session == null){
            return null;
        }
        return session.getAttribute(key);
    }

    @Override
    public Collection<String> getAttributeKeys() {
        session = sessionDao.readSession(session.getId());
        if(session == null){
            return null;
        }
        return session.getAttributeKeys();
    }

    @Override
    public Serializable removeAttribute(String key) {
        session = sessionDao.readSession(session.getId());
        Serializable o = session.removeAttribute(key);
        sessionDao.update(session);
        return o;
    }

    @Override
    public void touch() {
        session = sessionDao.readSession(session.getId());
        session.touch();
        sessionDao.update(session);
    }

    @Override
    public Proxy getProxy() {
        session = sessionDao.readSession(session.getId());
        if(session == null){
            return null;
        }
        return session.getProxy();
    }

    @Override
    public void setProxy(Proxy proxy) {
        session = sessionDao.readSession(session.getId());
        session.setProxy(proxy);
        sessionDao.update(session);
    }

    @Override
    public Map<String,List<HttpCookie>> getCookies() {
        session = sessionDao.readSession(session.getId());
        if(session == null){
            return null;
        }
        return session.getCookies();
    }

    @Override
    public void setCookies(Map<String,List<HttpCookie>> o) {
        session = sessionDao.readSession(session.getId());
        session.setCookies(o);
        sessionDao.update(session);
    }
}

    