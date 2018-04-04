package com.zrk1000.crawler.visitor.okhttp;

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
 * 内存实现的CookieJar
 * Created by rongkang on 2017-08-20.
 */
public class OkHttpMemoryCookieJar implements CookieJar {

    private static Logger logger = LoggerFactory.getLogger(OkHttpMemoryCookieJar.class);

    private Map<String, List<Cookie>> cookieStore;

    public OkHttpMemoryCookieJar() {
        cookieStore = new HashMap();
    }

    public Map<String, List<Cookie>> getCookieStore() {
        return cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
        if (logger.isDebugEnabled()) {
            logger.debug("\nwrite cookies:" + httpUrl.host() + " --> " + cookies);
        }
        cookieStore.put(httpUrl.host(), cookies);
        //保存根域名cookie
        String rootDomain = RootDomainUtils.getRootDomain(httpUrl.host());
        List<Cookie> rootDomainCookieList = new ArrayList();
        for (Cookie cookie : cookies) {
            if (cookie.domain().equals(rootDomain)) {
                rootDomainCookieList.add(cookie);
            }
        }
        if (!rootDomainCookieList.isEmpty()) {
            cookieStore.put(rootDomain, rootDomainCookieList);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        //加载根域名的cookie
        String rootDomain = RootDomainUtils.getRootDomain(httpUrl.host());
        if (rootDomain != null) {
            List<Cookie> cookieList = cookieStore.get(rootDomain);
            for (Cookie cookie : cookieList) {
                if (!cookies.contains(cookie)) {
                    cookies.add(cookie);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("\nload cookies:" + httpUrl.host() + " --> " + cookies);
        }
        return cookies == null ? new ArrayList() : cookies;
    }
}
