package com.zrk1000.crawler.principal;

/**
 * 构建subject的身份票据
 * Created by rongkang on 2017-08-20.
 */
public interface Ticket {

    /**
     *  目标网站
     */
    String getDomain();

    /**
     *
     * @param domain
     */
    void setDomain(String domain);

    /**
     * 访问者标识
     * @return
     */
    String getIdentify();

    /**
     *
     * @param identify
     */
    void setIdentify(String identify);


}
