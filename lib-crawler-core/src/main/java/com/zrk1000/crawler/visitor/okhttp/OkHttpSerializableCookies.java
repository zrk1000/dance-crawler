package com.zrk1000.crawler.visitor.okhttp;

import okhttp3.Cookie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * 支持jdk序列化的Cookies
 * User: zhouRongKang
 * Date: 2017/8/23
 * Time: 14:47
 */
@Deprecated
public class OkHttpSerializableCookies implements Serializable {

    private transient Cookie cookies;
//    private transient HttpCookie clientCookies;

    public OkHttpSerializableCookies(Cookie cookies) {
        this.cookies = cookies;
    }

    public Cookie getCookies() {
        Cookie bestCookies = cookies;
//        if (clientCookies != null) {
//            bestCookies = clientCookies;
//        }
        return bestCookies;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookies.name());
        out.writeObject(cookies.value());
        out.writeLong(cookies.expiresAt());
        out.writeObject(cookies.domain());
        out.writeObject(cookies.path());
        out.writeBoolean(cookies.secure());
        out.writeBoolean(cookies.httpOnly());
        out.writeBoolean(cookies.hostOnly());
        out.writeBoolean(cookies.persistent());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        long expiresAt = in.readLong();
        String domain = (String) in.readObject();
        String path = (String) in.readObject();
        boolean secure = in.readBoolean();
        boolean httpOnly = in.readBoolean();
        boolean hostOnly = in.readBoolean();
        boolean persistent = in.readBoolean();
        Cookie.Builder builder = new Cookie.Builder();
        builder = builder.name(name);
        builder = builder.value(value);
        builder = builder.expiresAt(expiresAt);
        builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
        builder = builder.path(path);
        builder = secure ? builder.secure() : builder;
        builder = httpOnly ? builder.httpOnly() : builder;
        cookies =builder.build();
    }

    @Override
    public String toString() {
        return "OkHttpSerializableCookies{" +
                "cookies=" + cookies +
                '}';
    }

    /**
     * 名称相同即认为cookie相同
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OkHttpSerializableCookies cookies1 = (OkHttpSerializableCookies) o;
        return Objects.equals(cookies.name(), cookies1.cookies.name());
    }

    /**
     * 重写hashcode
     * 名称相同即认为cookie相同
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(cookies.name());
    }
}

    