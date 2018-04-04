package com.zrk1000.crawler.visitor.http;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义http RequestBody
 * User: zhouRongKang
 * Date: 2017/8/28
 * Time: 15:25
 */
public class HttpRequestBody implements Serializable {

    private static final long serialVersionUID = 5659170945717023595L;

    private static final String DEF_CHARSET = "UTF-8";

    public static abstract class ContentType {

        public static final String JSON = "application/json";

        public static final String XML = "text/xml";

        public static final String FORM = "application/x-www-form-urlencoded";

        public static final String MULTIPART = "multipart/form-data";
    }

    /**
     * 请求体
     */
    private byte[] body;
    /**
     * 请求体contentType
     */
    private String contentType;
    /**
     * 请求字符集
     */
    private String charset;
    /**
     * 请求体参数
     */
    private Map<String, Object> params = new HashMap();
//    /**
//     * 是否已经编码
//     */
//    private boolean alreadyEncoded;


    public HttpRequestBody() {
    }

    public HttpRequestBody(byte[] body, String contentType) {
        this.body = body;
        this.contentType = contentType;
//        this.alreadyEncoded = false;
    }

    public HttpRequestBody(Map<String, Object> params, String contentType, String charset) {
        this.params = params;
        this.contentType = contentType;
        this.charset = charset;
//        this.alreadyEncoded = charset != null;

    }

    public String getContentType() {
        return contentType;
    }

//    public boolean isAlreadyEncoded() {
//        return alreadyEncoded;
//    }
//
//    public void setAlreadyEncoded(boolean alreadyEncoded) {
//        this.alreadyEncoded = alreadyEncoded;
//    }

    public String getCharset() {
        return charset;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    public void setParam(String key, Object value) {
        if (this.params == null) {
            this.params = new HashMap();
        }
        this.params.put(key, value);
    }


    /**快捷方法*/
    /**
     * 针对application/x-www-form-urlencoded类型
     */
    public static HttpRequestBody form(Map<String, Object> params) {
        return form(params, DEF_CHARSET);
    }

    public static HttpRequestBody form(Map<String, Object> params, String charset) {
        return new HttpRequestBody(params, ContentType.FORM, charset);
    }

    /**
     * 针对multipart/form-data类型
     */
    public static HttpRequestBody multipart(Map<String, Object> params) {
        return multipart(params, DEF_CHARSET);
    }

    public static HttpRequestBody multipart(Map<String, Object> params, String charset) {
        return new HttpRequestBody(params, ContentType.MULTIPART, charset);
    }

    /**
     * 针对application/json类型
     */
    public static HttpRequestBody json(String json) {
        return json(json, DEF_CHARSET);
    }

    public static HttpRequestBody json(String json, String charset) {
        return new HttpRequestBody(getByte(json, charset), ContentType.JSON);
    }

    /**
     * 针对application/xml类型
     */
    public static HttpRequestBody xml(String xml) {
        return xml(xml);
    }

    public static HttpRequestBody xml(String xml, String charset) {
        return new HttpRequestBody(getByte(xml, charset), ContentType.XML);
    }

    /**
     * 针对自定义类型
     */
    public static HttpRequestBody custom(byte[] body, String contentType) {
        return custom(body, contentType);
    }

    public static HttpRequestBody custom(String queryString, String contentType) {
        return custom(queryString, contentType, DEF_CHARSET);
    }

    public static HttpRequestBody custom(String string, String contentType, String charset) {
        return new HttpRequestBody(getByte(string, charset), contentType);
    }

    public static HttpRequestBody custom(Map<String, Object> params, String contentType) {
        return custom(params, contentType, DEF_CHARSET);
    }

    public static HttpRequestBody custom(Map<String, Object> params, String contentType, String charset) {
        return new HttpRequestBody(params, contentType, charset);
    }


    private static byte[] getByte(String str, String charset) {
        if (str != null) {
            try {
                return charset != null && !"".equals(charset) ? str.getBytes(charset) : str.getBytes();
            } catch (UnsupportedEncodingException e) {
                return str.getBytes();
            }
        } else {
            return new byte[0];
        }
    }


    /**
     * Builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    private HttpRequestBody(Builder builder) {
        setBody(builder.body);
        setContentType(builder.contentType);
        setCharset(builder.charset);
        setParams(builder.params);
//        setAlreadyEncoded(builder.alreadyEncoded);
    }


    public static final class Builder {
        private byte[] body;
        private String contentType;
        private String charset;
        private Map<String, Object> params;
//        private boolean alreadyEncoded;

        public Builder() {
        }

        public Builder body(byte[] val) {
            body = val;
            return this;
        }

        public Builder contentType(String val) {
            contentType = val;
            return this;
        }

        public Builder charset(String val) {
            charset = val;
            return this;
        }

        public Builder params(Map<String, Object> val) {
            params = val;
            return this;
        }

//        public Builder alreadyEncoded(boolean val) {
//            alreadyEncoded = val;
//            return this;
//        }

        /**
         * 个人定义开始
         */
        public Builder param(String key, Object value) {
            if (this.params == null) {
                this.params = new HashMap();
            }
            this.params.put(key, value);
            return this;
        }

        public Builder form(Map<String, Object> params) {
            form(params, DEF_CHARSET);
            return this;
        }

        public Builder form(Map<String, Object> params, String charset) {
            this.params = params;
            this.charset = charset;
            this.contentType = ContentType.FORM;
            return this;
        }

        public Builder json(String json) {
            json(json, DEF_CHARSET);
            return this;
        }

        public Builder json(String json, String charset) {
            this.body = getByte(json, charset);
            this.charset = charset;
            this.contentType = ContentType.JSON;
            return this;
        }

        public Builder multipart(Map<String, Object> params) {
            multipart(params, DEF_CHARSET);
            return this;
        }

        public Builder multipart(Map<String, Object> params, String charset) {
            this.params = params;
            this.charset = charset;
            this.contentType = ContentType.MULTIPART;
            return this;
        }

        public Builder xml(String xml, String charset) {
            this.body = getByte(xml, charset);
            this.charset = charset;
            this.contentType = ContentType.XML;
            return this;
        }

        public Builder xml(String xml) {
            xml(xml, DEF_CHARSET);
            return this;
        }

        /**
         * 个人定义结束
         */

        public HttpRequestBody build() {
            return new HttpRequestBody(this);
        }
    }


//    public static final class Builder {
//        private static final String DEF_CHARSET = "UTF-8";
//        private byte[] body;
//        private String contentType;
//        private String encoding;
//        private Map<String, Object> params;
//        private boolean encode;
//
//        public Builder() {
//        }
//
//        public Builder json(String json, String encoding) {
//            this.body = getByte(json, encoding);
//            this.encoding = encoding;
//            this.contentType = ContentType.JSON;
//            return this;
//        }
//
//        public Builder json(String json) {
//            json(json, DEF_CHARSET);
//            return this;
//        }
//
//        public Builder multipart(String multipart, String encoding) {
//            this.body = getByte(multipart, encoding);
//            this.encoding = encoding;
//            this.contentType = ContentType.MULTIPART;
//            return this;
//        }
//
//        public Builder multipart(String multipart) {
//            multipart(multipart, DEF_CHARSET);
//            return this;
//        }
//
//
//        public Builder xml(String xml, String encoding) {
//            this.body = getByte(xml, encoding);
//            this.encoding = encoding;
//            this.contentType = ContentType.XML;
//            return this;
//        }
//
//        public Builder xml(String xml) {
//            xml(xml, DEF_CHARSET);
//            return this;
//        }
//
//        public Builder params(Map<String, Object> val) {
//            params = val;
//            return this;
//        }
//
//        public Builder param(String key, Object value) {
//            if (this.params == null) {
//                this.params = new HashMap();
//            }
//            this.params.put(key, value);
//            return this;
//        }
//
//        public Builder encode(boolean encode) {
//            this.encode = encode;
//            return this;
//        }
//
//        public Builder body(byte[] val) {
//            body = val;
//            return this;
//        }
//
//        public Builder contentType(String val) {
//            contentType = val;
//            return this;
//        }
//
//        public Builder encoding(String val) {
//            encoding = val;
//            return this;
//        }
//        public static HttpRequestBody.Builder newBuilder() {
//            return new HttpRequestBody.Builder();
//        }
//        public HttpRequestBody build() {
//            return new HttpRequestBody(this);
//        }
//    }


}
