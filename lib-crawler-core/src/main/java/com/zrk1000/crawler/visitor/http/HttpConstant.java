package com.zrk1000.crawler.visitor.http;

/**
 * http 常量定义
 * User: zhouRongKang
 * Date: 2017/8/28
 * Time: 14:22
 */
public abstract class HttpConstant {

    public static abstract class Method {

        public static final String GET = "GET";

//        public static final String HEAD = "HEAD";

        public static final String POST = "POST";

        public static final String PUT = "PUT";

        public static final String DELETE = "DELETE";

//        public static final String TRACE = "TRACE";

//        public static final String CONNECT = "CONNECT";

    }

    public static abstract class StatusCode {

        public static final int CODE_200 = 200;

    }

    public static abstract class Header {

        public static final String REFERER = "Referer";

        public static final String USER_AGENT = "User-Agent";
    }

}
