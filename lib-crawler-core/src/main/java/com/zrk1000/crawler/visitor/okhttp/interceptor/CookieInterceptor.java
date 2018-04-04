package com.zrk1000.crawler.visitor.okhttp.interceptor;

import com.zrk1000.crawler.visitor.OkHttpVisitor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 兼容cookieJar 与 手动设置cookie拦截器
 * 放到请求头key为MyCookie的值可被此拦截器追加到Cookie中
 *
 * @author zhouRongKang
 * @date 2017/12/28 14:13
 */
public class CookieInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request userRequest = chain.request();
        Request.Builder requestBuilder = userRequest.newBuilder();
        String myCookie = userRequest.header(OkHttpVisitor.COOKIE_KEY);
        if (myCookie != null) {
            if (userRequest.header("Cookie") != null) {
                requestBuilder.header("Cookie", userRequest.header("Cookie") + ";" + myCookie);
            } else {
                requestBuilder.header("Cookie", myCookie);
            }
            requestBuilder.removeHeader(OkHttpVisitor.COOKIE_KEY);
        }
        Response response = chain.proceed(requestBuilder.build());
        return response;
    }
}
