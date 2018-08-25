package com.zrk1000.crawler.visitor.okhttp;

import com.zrk1000.crawler.session.Session;
import com.zrk1000.crawler.util.HttpCookieUtils;
import com.zrk1000.crawler.util.RootDomainUtils;
import com.zrk1000.crawler.visitor.http.HttpCookie;
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
        Map<String, List<HttpCookie>> cookieStore = session.getCookies();
        if (cookieStore == null) {
            cookieStore = new HashMap();
        }
        List<HttpCookie> httpCookies = cookieStore.get(httpUrl.host());
        httpCookies = HttpCookieUtils.addCookie(httpCookies, HttpCookieUtils.cookies2HttpCookies(cookies));
        cookieStore.put(httpUrl.host(), httpCookies);

        /**保存根域名cookie*/
        String rootDomain = RootDomainUtils.getRootDomain(httpUrl.host());
        List<Cookie> rootDomainCookies = new ArrayList();
        for (Cookie cookie : cookies) {
            if (cookie.domain().equals(rootDomain)) {
                rootDomainCookies.add(cookie);
            }
        }
        if (!rootDomainCookies.isEmpty()) {
            List<HttpCookie> rootHttpCookies = cookieStore.get(rootDomain);
            cookieStore.put(rootDomain, HttpCookieUtils.addCookie(rootHttpCookies,HttpCookieUtils.cookies2HttpCookies(rootDomainCookies)));
        }
        session.setCookies(cookieStore);

    }

    public static void main(String[] args) {
        Map<String, List<HttpCookie>> cookieStore  =  new HashMap();
        List<HttpCookie> httpCookies = cookieStore.get("123");
        System.out.println(httpCookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        Map<String, List<HttpCookie>> cookieStore = session.getCookies();
        List<Cookie> cookies = loadCookie(cookieStore, httpUrl.host());
        /**加载根域名的cookie*/
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
    private List<Cookie> loadCookie(Map<String, List<HttpCookie>> cookieStore, String host) {
        if (cookieStore == null) {
            return new ArrayList();
        }
        List<HttpCookie> httpCookies = cookieStore.get(host);
        List<Cookie> cookies = httpCookies == null ? new ArrayList() : HttpCookieUtils.httpCookies2cookies(httpCookies);
        return cookies;
    }


}
