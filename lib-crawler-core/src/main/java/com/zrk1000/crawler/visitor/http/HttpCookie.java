package com.zrk1000.crawler.visitor.http;

import java.io.Serializable;
import java.util.Objects;

/**
 * 自定义cookie
 * User: zhouRongKang
 * Date: 2018/4/24
 * Time: 14:22
 */
public class HttpCookie implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    private String name;
    private String value;
    private Long expiresAt;
    private String domain;
    private String path;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
        this.path = "/";
    }

    public HttpCookie(String name, String value, String path) {
        this.name = name;
        this.value = value;
        this.path = path;
    }

    public HttpCookie(String name, String value, Long expiresAt, String domain, String path) {
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
        this.domain = domain;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 重写equals，只比较name和path
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, path);
    }
}
