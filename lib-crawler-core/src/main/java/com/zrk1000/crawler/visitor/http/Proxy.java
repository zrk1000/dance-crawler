package com.zrk1000.crawler.visitor.http;

import java.io.Serializable;

/**
 * 代理
 * @author zhouRongKang
 * @date 2017/11/10 16:51
 */
public class Proxy implements Serializable{

    private String ip;

    private Integer prot;

    public Proxy() {
    }

    public Proxy(String ip, Integer prot) {
        this.ip = ip;
        this.prot = prot;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getProt() {
        return prot;
    }

    public void setProt(Integer prot) {
        this.prot = prot;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "ip='" + ip + '\'' +
                ", prot=" + prot +
                '}';
    }
}
