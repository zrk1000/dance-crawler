package com.zrk1000.crawler.subject;

import com.zrk1000.crawler.pipeline.Pipeline;
import com.zrk1000.crawler.principal.SimpleSubject;
import com.zrk1000.crawler.principal.Ticket;
import com.zrk1000.crawler.processor.ProcessorRegister;
import com.zrk1000.crawler.session.SessionManager;
import com.zrk1000.crawler.visitor.Visitor;

import java.util.List;

/**
 * 公积金访问者
 * User: zhouRongKang
 * Date: 2017/8/31
 * Time: 13:54
 */
public class MySubject extends SimpleSubject{

    public MySubject(Ticket ticket, ProcessorRegister processorRegister, SessionManager sessionManager, Visitor visitor, List<Pipeline> pipelines) {
        super(ticket, processorRegister, sessionManager, visitor,pipelines);
    }

    public Object login() {
        return executor().method("login").execute();
    }

    public Object getCaptcha() {
        return executor().method("captcha").execute();
    }

    public Object parse() {
        return executor().method("parse").execute();
    }

}

    