package com.zrk1000.crawler.crawler;

import com.zrk1000.crawler.SimpleCrawler;
import com.zrk1000.crawler.exception.CrawlerException;
import com.zrk1000.crawler.subject.MySubject;
import com.zrk1000.crawler.ticket.MyTicket;

/**
 * 公积金爬虫
 * User: zhouRongKang
 * Date: 2017/8/31
 * Time: 14:01
 */
public class MyCrawler extends SimpleCrawler<MySubject,MyTicket> {

    /**
     * 默认不指定处理器注册中心
     */
    public MyCrawler() {
        super();
    }

    /**
     * 默认使用SimpleProcessorRegister处理器注册中心
     * @param processorPackage
     */
    public MyCrawler(String processorPackage) {
        super(processorPackage);
    }

    @Override
    public MySubject getSubject(MyTicket token) {
        if(processorRegister == null){
            throw CrawlerException.newInstance(651," The processorRegister must not be empty");
        }
        if(sessionManager == null){
            throw CrawlerException.newInstance(652," The sessionManager must not be empty");
        }
        return new MySubject(token, processorRegister,sessionManager, visitorGenerator.generate(), pipelines);
    }
}

    