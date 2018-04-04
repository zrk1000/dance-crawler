/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.zrk1000.crawler.visitor.http;

import com.zrk1000.crawler.util.CharsetDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义 http Response
 * User: zhouRongKang
 * Date: 2017/8/27
 * Time: 14:56
 */
public class HttpResponse {

    public static final Logger LOG = LoggerFactory.getLogger(HttpResponse.class);

    private HttpRequest request;
    private String contentType;
    private Integer code = null;
    private Exception exception = null;
    private String charset = null;
    private byte[] content = null;
    private Object obj = null;
    private Page page;

    public HttpResponse(Integer code, String contentType, byte[] content){
        this.code = code;
        this.contentType = contentType;
        this.content = content;
    }

    public Page page(){
        return Page.transform(this);
    }


    /**
     * 返回网页/文件的内容
     *
     * @return 网页/文件的内容
     */
    public byte[] content() {
        return content;
    }

    public void content(byte[] content){
        this.content = content;
    }

    public String url() {
        return this.request.getUrl();
    }

    public String contentType() {
        return contentType;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String charset() {
        if (charset == null && content() != null) {
            charset = CharsetDetector.guessEncoding(content());
        }
        return charset;
    }

    public void charset(String charset) {
        this.charset = charset;
    }


    public void code(int code){
        this.code = code;
    }

    public int code() {
        return code;
    }


    public <T> T obj() {
        return (T)obj;
    }

    public void obj(Object obj) {
        this.obj = obj;
    }

    public HttpRequest request() {
        return request;
    }

    public void request(HttpRequest request) {
        this.request = request;
    }
}
