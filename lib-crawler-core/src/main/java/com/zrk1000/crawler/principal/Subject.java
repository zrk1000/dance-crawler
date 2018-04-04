package com.zrk1000.crawler.principal;

import com.zrk1000.crawler.session.Session;

import java.io.Serializable;

/**
 * 访问者
 * Created by rongkang on 2017-07-11.
 */
public interface Subject extends Serializable {


    /**
     * 身份票据
     * @return
     */
    Ticket getTicket();

    /**
     * 会话
     * @return
     */
    Session getSession();

    /**
     * 执行器
     * @return
     */
    SubjectExecutor executor();



}
