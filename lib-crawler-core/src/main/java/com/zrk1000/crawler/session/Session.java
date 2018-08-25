package com.zrk1000.crawler.session;

import com.zrk1000.crawler.visitor.http.HttpCookie;
import com.zrk1000.crawler.visitor.http.Proxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * session接口
 * Created by rongkang on 2017-07-11.
 */
public interface Session extends Serializable {

    String getId();

    Date getStartTimestamp();

    Date getLastAccessTime();

    long getTimeout();

    void setTimeout(long maxIdleTimeInMillis);

    void setAttribute(String key, Serializable value);

    Serializable getAttribute(String key);

    Collection<String> getAttributeKeys();

    Serializable removeAttribute(String key);

    void touch();

    Proxy getProxy();

    void setProxy(Proxy proxy);

    Map<String,List<HttpCookie>> getCookies();

    void setCookies(Map<String,List<HttpCookie>> o);

}
