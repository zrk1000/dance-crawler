package com.zrk1000.crawler.visitor.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义http request
 * User: zhouRongKang
 * Date: 2017/8/28
 * Time: 15:29
 */
public class HttpRequest implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    private String url;

    private String method;

    private HttpRequestBody requestBody;

    private Map<String, String> cookies = new HashMap<String, String>();

    private Map<String, String> headers = new HashMap<String, String>();

    private Proxy proxy;

    private int deep;

    private String charset;

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.url = url;
    }

    private HttpRequest(Builder builder) {
        setUrl(builder.url);
        setMethod(builder.method);
        setRequestBody(builder.requestBody);
        setCookies(builder.cookies);
        setHeaders(builder.headers);
        setDeep(builder.deep);
        setProxy(builder.proxy);
        setCharset(builder.charset);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public HttpRequest setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public String getUrl() {
        return url;
    }


    public HttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }
    public String getMethod() {
        return method;
    }

    public HttpRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        HttpRequest request = (HttpRequest) o;

        if (url != null ? !url.equals(request.url) : request.url != null) {
            return false;
        }
        return method != null ? method.equals(request.method) : request.method == null;
    }

    public HttpRequest addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    public HttpRequest addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpRequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(HttpRequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public String getCharset() {
        return charset;
    }

    public HttpRequest setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", cookies="+ cookies+
                '}';
    }


    public static final class Builder {
        private String url;
        private String method;
        private HttpRequestBody requestBody;
        private Map<String, String> cookies;
        private Map<String, String> headers;
        private Proxy proxy;
        private int deep;
        private String charset;

        private Builder() {
        }
        public Builder get(String url) {
            this.url = url;
            this.method = HttpConstant.Method.GET;
            return this;
        }

        public Builder post(String url) {
            this.url = url;
            this.method = HttpConstant.Method.POST;
            return this;
        }

        public Builder put(String url) {
            this.url = url;
            this.method = HttpConstant.Method.PUT;
            return this;
        }

        public Builder delete(String url) {
            this.url = url;
            this.method = HttpConstant.Method.DELETE;
            return this;
        }

        public Builder requestBody(HttpRequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Builder param(String key,Object value) {
            if(this.requestBody == null){
                this.requestBody = new HttpRequestBody();
                this.requestBody.setContentType(HttpRequestBody.ContentType.FORM);
            }
            this.requestBody.setParam(key,value);
            return this;
        }

        public Builder params(Map<String, Object> params) {
            if(params!=null){
                for (String key:params.keySet()){
                    param(key,params.get(key));
                }
            }
            return this;
        }

        public Builder cookies(Map<String, String> val) {
            cookies = val;
            return this;
        }

        public Builder cookie(String key,String value) {
            if(this.cookies == null){
                this.cookies = new HashMap();
            }
            this.cookies.put(key,value);
            return this;
        }

        public Builder headers(Map<String, String> val) {
            headers = val;
            return this;
        }

        public Builder header(String key,String value) {
            if(this.headers == null){
                this.headers = new HashMap();
            }
            this.headers.put(key,value);
            return this;
        }

        public Builder proxy(Proxy val) {
            proxy = val;
            return this;
        }

        public Builder deep(int val) {
            deep = val;
            return this;
        }

        public Builder charset(String val) {
            charset = val;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
