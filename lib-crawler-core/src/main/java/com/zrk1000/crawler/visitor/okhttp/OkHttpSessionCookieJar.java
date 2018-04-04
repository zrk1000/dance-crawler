package com.zrk1000.crawler.visitor.okhttp;

import com.zrk1000.crawler.session.Session;
import com.zrk1000.crawler.util.RootDomainUtils;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * session实现的CookieJar
 * Created by rongkang on 2017-08-23.
 */
public class OkHttpSessionCookieJar implements CookieJar {
    private static Logger logger = LoggerFactory.getLogger(OkHttpSessionCookieJar.class);


    private Session session;

    public OkHttpSessionCookieJar(Session session) {
        this.session = session;
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
        if (logger.isDebugEnabled()) {
            logger.debug("\nwrite cookies:" + httpUrl.host() + " --> " + cookies);
        }
        HashMap<String, List<OkHttpSerializableCookies>> cookieStore = (HashMap<String, List<OkHttpSerializableCookies>>) session.getCookies();
        if (cookieStore == null) {
            cookieStore = new HashMap();
        }
        cookieStore.put(httpUrl.host(), cookiesSerial(cookies));
        //保存根域名cookie
        String rootDomain = RootDomainUtils.getRootDomain(httpUrl.host());
        List<Cookie> rootDomainCookieList = new ArrayList();
        for (Cookie cookie : cookies) {
            if (cookie.domain().equals(rootDomain)) {
                rootDomainCookieList.add(cookie);
            }
        }
        if (!rootDomainCookieList.isEmpty()) {
            cookieStore.put(rootDomain, cookiesSerial(rootDomainCookieList));
        }
        session.setCookies(cookieStore);

    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        Map<String, List<OkHttpSerializableCookies>> cookieStore = (Map<String, List<OkHttpSerializableCookies>>) session.getCookies();
        List<Cookie> cookies = loadCookie(cookieStore, httpUrl.host());
        //加载根域名的cookie
        String rootDomain = RootDomainUtils.getRootDomain(httpUrl.host());
        if (rootDomain != null) {
            List<Cookie> cookieList = loadCookie(cookieStore, rootDomain);
            for (Cookie cookie : cookieList) {
                if (!cookies.contains(cookie)) {
                    cookies.add(cookie);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("\nload cookies--->{}:{}", httpUrl.host(), cookies);
        }
        return cookies;
    }

    /**
     * 获取cookieStore中的对应的cookie
     *
     * @param cookieStore
     * @param host
     * @return
     */
    private List<Cookie> loadCookie(Map<String, List<OkHttpSerializableCookies>> cookieStore, String host) {
        if (cookieStore == null) {
            return new ArrayList();
        }
        List<OkHttpSerializableCookies> cookies = cookieStore.get(host);
        List<Cookie> cookiesUnSerial = cookies == null ? new ArrayList() : cookiesUnSerial(cookies);
        return cookiesUnSerial;
    }

    /**
     * cookie转为可序列化的OkHttpSerializableCookies
     *
     * @param cookies
     * @return
     */
    private List<OkHttpSerializableCookies> cookiesSerial(List<Cookie> cookies) {
        if (cookies == null) {
            return null;
        }
        List<OkHttpSerializableCookies> cookiesList = new ArrayList();
        for (Cookie cookie : cookies) {
            cookiesList.add(new OkHttpSerializableCookies(cookie));
        }
        return cookiesList;
    }

    /**
     * OkHttpSerializableCookies转化为cookie
     *
     * @param cookies
     * @return
     */
    private List<Cookie> cookiesUnSerial(List<OkHttpSerializableCookies> cookies) {
        if (cookies == null) {
            return null;
        }
        List<Cookie> cookiesList = new ArrayList();
        for (OkHttpSerializableCookies cookie : cookies) {
            cookiesList.add(cookie.getCookies());
        }
        return cookiesList;
    }
}
