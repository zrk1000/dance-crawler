package com.zrk1000.crawler.session;

import com.zrk1000.crawler.visitor.http.HttpCookie;
import com.zrk1000.crawler.visitor.http.Proxy;

import java.io.Serializable;
import java.util.*;

/**
 * 默认的session实现
 * @author : zhouRongKang
 * @Date: 2017/8/21
 * @Time: 12:37
 */
public class SimpleSession implements Session {

    private String sessionId;
    private Date startTimestamp;
    private Date lastAccessTime;
    private long timeout;
    private Proxy proxy;
    private Map<String,List<HttpCookie>> cookies;
    private Map<String, Serializable> attributes ;


    public SimpleSession() {
        this.startTimestamp = new Date();
        this.attributes = new HashMap();
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public Date getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long maxIdleTimeInMillis) {
        this.timeout = maxIdleTimeInMillis;
    }

    @Override
    public void setAttribute(String key, Serializable value) {
        this.attributes.put(key,value);
    }

    @Override
    public Serializable getAttribute(String key) {
        return this.attributes.get(key);
    }

    @Override
    public Collection<String> getAttributeKeys() {
        return this.attributes.keySet();
    }

    @Override
    public Serializable removeAttribute(String key) {
        return this.attributes.remove(key);
    }

    @Override
    public void touch() {
        this.lastAccessTime = new Date();
    }

    @Override
    public Proxy getProxy() {
        return proxy;
    }

    @Override
    public Map<String,List<HttpCookie>> getCookies() {
        return cookies;
    }

    @Override
    public void setCookies(Map<String,List<HttpCookie>> cookies) {
        this.cookies = cookies;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getSessionId() {
        return sessionId;
    }
}

    