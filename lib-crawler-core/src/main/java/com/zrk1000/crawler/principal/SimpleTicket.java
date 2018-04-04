package com.zrk1000.crawler.principal;

import java.io.Serializable;

/**
 * 访问者token
 * Created by rongkang on 2017-08-20.
 */
public class SimpleTicket implements Ticket,Serializable {

    protected String identify;        //访问者标识

    protected String domain;          //目标网站标识

    @Override
    public String getIdentify() {
        return identify;
    }

    @Override
    public void setIdentify(String identify) {
        this.identify =identify;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
