package com.zrk1000.crawler.util;

import com.zrk1000.crawler.visitor.http.HttpCookie;
import okhttp3.Cookie;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义HttpCookie工具类
 * Created by rongkang on 2018-05-27.
 */
public class HttpCookieUtils {

    /**
     * cookie转为HttpCookies
     *
     * @param cookies
     * @return
     */
    public static List<HttpCookie> cookies2HttpCookies(List<Cookie> cookies) {
        if (cookies == null) {
            return null;
        }
        List<HttpCookie> cookiesList = new ArrayList();
        for (Cookie cookie : cookies) {
            cookiesList.add(new HttpCookie(cookie.name(),cookie.value(),cookie.expiresAt(),cookie.domain(),cookie.path()));
        }
        return cookiesList;
    }

    /**
     * HttpCookies转化为cookie
     *
     * @param cookies
     * @return
     */
    public static List<Cookie> httpCookies2cookies(List<HttpCookie> cookies) {
        if (cookies == null) {
            return null;
        }
        List<Cookie> cookiesList = new ArrayList();
        for (HttpCookie httpCookie : cookies) {
            Cookie cookie = new Cookie.Builder()
                    .name(httpCookie.getName())
                    .value(httpCookie.getValue())
                    .expiresAt(httpCookie.getExpiresAt())
                    .domain(httpCookie.getDomain())
                    .path(httpCookie.getPath())
                    .build();
            cookiesList.add(cookie);
        }
        return cookiesList;
    }

    /**
     * 添加cookie，若name相同，则覆盖
     * @param httpCookies
     * @param newCookies
     * @return
     */
    public static List<HttpCookie> addCookie(List<HttpCookie> httpCookies, List<HttpCookie> newCookies) {
        if(newCookies == null ){
            return httpCookies;
        }
        if(httpCookies == null){
            return newCookies;
        }
        newCookies.stream().forEach(item -> {
            if(httpCookies.contains(item)){
                httpCookies.remove(item);
            }
            httpCookies.add(item);
        });
        return httpCookies;
    }
}
