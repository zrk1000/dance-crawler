package com.zrk1000.crawler.visitor;

import com.zrk1000.crawler.exception.CrawlerException;
import com.zrk1000.crawler.principal.Ticket;
import com.zrk1000.crawler.principal.TicketAware;
import com.zrk1000.crawler.session.Session;
import com.zrk1000.crawler.session.SessionAware;
import com.zrk1000.crawler.visitor.http.*;
import com.zrk1000.crawler.visitor.okhttp.OKHttpPool;
import com.zrk1000.crawler.visitor.okhttp.OkHttpMemoryCookieJar;
import com.zrk1000.crawler.visitor.okhttp.OkHttpSessionCookieJar;
import okhttp3.*;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 网络访问器-okhttp实现
 * Created by rongkang on 2017-08-27.
 */
public class OkHttpVisitor implements Visitor, SessionAware, TicketAware {

    private Logger logger = LoggerFactory.getLogger(OkHttpVisitor.class);

    public static final String COOKIE_KEY = "MyCookie";

    private OkHttpClient.Builder clientBuilder;
    private ProxyFactory proxyFactory;
    private Proxy proxy;
    private CookieJar cookieJar;
    private int connectTimeout = 10;
    private int readTimeout = 20;
    private int writeTimeout = 10;
    private boolean isRedirect = true;
    private Ticket ticket;
    private Session session;
    private List<Interceptor> interceptors;
    private List<Interceptor> networkInterceptors;

    public OkHttpVisitor() {
        this(new OKHttpPool());
    }

    public OkHttpVisitor(OKHttpPool okHttpPool) {
        this.clientBuilder = okHttpPool.getClientBuilder();
        this.cookieJar = new OkHttpMemoryCookieJar();
    }

    private void initClient() {
        OkHttpClient.Builder builder = clientBuilder
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .followRedirects(isRedirect)
                .followSslRedirects(isRedirect);
        if (proxy == null && session != null) {
            proxy = session.getProxy();
        }
        if (proxy == null && proxyFactory != null && ticket != null) {
            proxy = proxyFactory.getProxy(ticket);
        }
        if (proxy != null) {
            builder.proxy(transformProxy(proxy));
        }
        if (proxy != null && session != null) {
            session.setProxy(proxy);
        }
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }
        if (networkInterceptors != null && !networkInterceptors.isEmpty()) {
            for (Interceptor networkInterceptor : networkInterceptors) {
                builder.networkInterceptors().remove(networkInterceptor);
                builder.addNetworkInterceptor(networkInterceptor);
            }
        }
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                builder.interceptors().remove(interceptor);
                builder.addInterceptor(interceptor);
            }
        }
    }


    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
        initClient();
    }

    public void setNetworkInterceptors(List<Interceptor> networkInterceptors) {
        this.networkInterceptors = networkInterceptors;
        initClient();
    }

    private java.net.Proxy transformProxy(Proxy proxy) {
        return new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getIp(), proxy.getProt()));
    }

    @Override
    public void setSession(Session session) {
        this.cookieJar = new OkHttpSessionCookieJar(session);
        this.session = session;
        initClient();
    }

    @Override
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        initClient();
    }

    /**
     * 访问器入口方法
     * @param httpRequest
     * @return
     */
    @Override
    public HttpResponse visit(HttpRequest httpRequest) {
        //请求转换
        Request request = transformHttpRequest(httpRequest);
        //构建原生的okHttpClient，并附加代理
        OkHttpClient okHttpClient = httpRequest.getProxy() == null ? clientBuilder.build() :
                clientBuilder.build().newBuilder().proxy(transformProxy(httpRequest.getProxy())).build();
        logger.debug("start request>>>>>>>>>>>>>>>>>>>>>");
        //发起请求
        Response response = getResponse(okHttpClient, request);
        logger.debug("end request<<<<<<<<<<<<<<<<<<<<");
        return transformResponse(response, httpRequest);
    }

    private HttpResponse transformResponse(Response response, HttpRequest httpRequest) {
        ResponseBody responseBody = null;
        int code = -1;
        byte[] content = null;
        String contentType = null;
        String charset = null;
        if (response != null) {
            responseBody = response.body();
            code = response.code();
        }
        if (responseBody != null) {
            MediaType mediaType = responseBody.contentType();
            if (mediaType != null) {
                contentType = mediaType.toString();
                charset = mediaType.charset() != null ? mediaType.charset().name() : null;
            }
            try {
                content = responseBody.bytes();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        HttpResponse httpResponse = new HttpResponse(code, contentType, content);
        httpResponse.charset(charset);
        httpResponse.request(httpRequest);
        return httpResponse;
    }

    /**
     * 将自定义的请求对象转换为okhttp的请求对象
     * @param httpRequest
     * @return
     */
    private Request transformHttpRequest(HttpRequest httpRequest) {
        Request.Builder requestBuilder = new Request.Builder();
        //将自定义的请求头转换为okhttp的请求头
        if (httpRequest.getHeaders() != null && !httpRequest.getHeaders().isEmpty() || httpRequest.getCookies() != null && !httpRequest.getCookies().isEmpty()) {
            requestBuilder.headers(transMapToHeaders(httpRequest.getHeaders(), httpRequest.getCookies()));
        }
        String method = httpRequest.getMethod();
        RequestBody requestBody = null;
        if (httpRequest.getRequestBody() != null) {
            if (httpRequest.getRequestBody().getContentType() == null) {
                throw new IllegalArgumentException("The ContentType cannot be empty");
            }
            HttpRequestBody httpRequestBody = httpRequest.getRequestBody();
            MediaType mediaType = MediaType.parse(httpRequestBody.getContentType());
            //普通form表单请求
            if (mediaType.equals(MediaType.parse(HttpRequestBody.ContentType.FORM))) {
                FormBody.Builder builder = new FormBody.Builder();
                if (httpRequestBody.getCharset() != null) {
                    //若设置字符集，进行编码
                    httpRequestBody.getParams().forEach((s, o) -> {
                        builder.addEncoded(s, urlEncode(String.valueOf(o),httpRequestBody.getCharset()));
                    });
                } else {
                    //若未设置字符集，直接设置
                    httpRequestBody.getParams().forEach((s, o) -> {
                        builder.add(s, String.valueOf(o));
                    });
                }
                requestBody = builder.build();
            //处理multipart/form-data格式请求，参数放在HttpRequestBody的params中
            } else if (mediaType.equals(MediaType.parse(HttpRequestBody.ContentType.MULTIPART))) {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (httpRequestBody.getParams() != null) {
                    Map<String, Object> map = httpRequestBody.getParams();
                    for (String key : map.keySet()) {
                        builder.addFormDataPart(key, String.valueOf(map.get(key)));
                    }
                    requestBody = builder.build();
                }

            } else {
                //其余的请求直接使用okhttp原生的RequestBody.create()进行构建
                if(httpRequestBody.getBody() != null){
                    requestBody = RequestBody.create(mediaType, httpRequestBody.getBody());
                }else  if(httpRequestBody.getParams() != null){
                    requestBody = RequestBody.create(mediaType, map2queryStr(httpRequestBody.getParams(),httpRequestBody.getCharset()));
                }
            }
        }
        //必需requestBody但requestBody为空的请求 赋值长度为0的请求体
        if (requestBody == null && HttpMethod.requiresRequestBody(method)) {
            requestBody = RequestBody.create(null, Util.EMPTY_BYTE_ARRAY);
        }
        //不需要requestBody的但requestBody不为空的请求 清空requestBody
        if (requestBody != null && !HttpMethod.permitsRequestBody(method)) {
            String url = httpRequest.getUrl();
            String urlParams = map2queryStr(httpRequest.getRequestBody().getParams() ,httpRequest.getCharset());
            url = url.contains("?") ? url + "&" + urlParams : url + "?" + urlParams;
            httpRequest.setUrl(url);
            requestBody = null;
        }
        return requestBuilder
                .url(httpRequest.getUrl())
                .method(method, requestBody)
                .build();
    }

    private String urlEncode(String url, String charset) {
        try {
            return URLEncoder.encode(url, charset == null || "".equals(charset) ? "UTF-8" : charset);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return url;
    }

    /**
     * 将自定义的请求头转换为okhttp的请求头
     * @param header
     * @param cookies
     * @return
     */
    private Headers transMapToHeaders(Map<String, String> header, Map<String, String> cookies) {
        if (header == null || header.isEmpty()) {
            header = new HashMap();
        }
        Headers.Builder builder = new Headers.Builder();
        if (cookies != null) {
            List<String> cookieList = new ArrayList();
            for (String cookieKey : cookies.keySet()) {
                cookieList.add(new StringBuffer().append(cookieKey).append("=").append(cookies.get(cookieKey)).toString());
            }
            String cookieStr = StringUtils.join(cookieList, ";");
            //将自定义的cookies赋值到自定义的请求头上，自定义key为：MyCookie
            if (header.containsKey(COOKIE_KEY)) {
                header.put(COOKIE_KEY, new StringBuffer().append(header.get(COOKIE_KEY)).append(";").append(cookieStr).toString());
            } else {
                header.put(COOKIE_KEY, cookieStr);
            }
        }
        header.forEach(builder::add);
        return builder.build();
    }

    private Response getResponse(OkHttpClient okHttpClient, Request request) {
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw CrawlerException.newInstance(658, "Network access failed , url : %s",request.url());
        }
    }

    public Proxy getProxy() {
        return proxy;
    }

    @Override
    public void setAutoRedirect(boolean allow) {
        clientBuilder = clientBuilder.followRedirects(allow).followSslRedirects(allow);
    }

    @Override
    public void closeSession() {
        session.setTimeout(1L);
    }

    @Override
    public void setTimeOut(Integer connectTimeout, Integer readTimeout, Integer writeTimeout) {
        if(connectTimeout != null){
            this.connectTimeout = connectTimeout;
        }
        if( readTimeout != null){
            this.readTimeout = readTimeout;
        }
        if(writeTimeout != null){
            this.writeTimeout = writeTimeout;
        }
        if(connectTimeout != null && readTimeout != null && writeTimeout != null){
            initClient();
        }
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public CookieJar getCookieJar() {
        return cookieJar;
    }

    public void setCookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
        initClient();
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    /**
     * 将url参数转换成map
     *
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public Map<String, Object> queryStr2Map(String param) {
        Map<String, Object> map = new HashMap();
        if (param == null || "".equals(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public String map2queryStr(Map<String, Object> map, String charset) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(charset != null){
                sb.append(entry.getKey() + "=" + urlEncode(String.valueOf(entry.getValue()),charset));
            }else {
                sb.append(entry.getKey() + "=" + entry.getValue());
            }
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }


}
