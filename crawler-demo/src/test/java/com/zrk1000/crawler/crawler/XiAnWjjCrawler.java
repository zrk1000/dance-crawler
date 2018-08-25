package com.zrk1000.crawler.crawler;

import com.zrk1000.crawler.processor.ServiceFactory;
import com.zrk1000.crawler.processor.SimpleProcessorRegister;
import com.zrk1000.crawler.processor.SimpleServiceFactory;
import com.zrk1000.crawler.subject.MySubject;
import com.zrk1000.crawler.ticket.MyTicket;
import org.junit.Before;
import org.junit.Test;


public class XiAnWjjCrawler {

    MyCrawler myCrawler;

    @Before
    public void before(){
        //初始化 - 创建爬虫对象
        myCrawler = new MyCrawler();
        ServiceFactory serviceFactory = new SimpleServiceFactory() ;
        //初始化 - 设置处理器注册中心 ， 使用Groovy脚本注册中心
        myCrawler.setProcessorRegister(new SimpleProcessorRegister("com.zrk1000.crawler",serviceFactory));
        //初始化 - 设置代理工厂
//        myCrawler.setProxyFactory(new MapProxyFactory());
    }

//    http://wjj.xa.gov.cn/ptl/def/def/index_1285_3887_ci_trid_4416419.html
    @Test
    public void wjjTest(){
        MyTicket myTicket = new MyTicket();
        myTicket.setDomain("wjj");
        MySubject subject = myCrawler.getSubject(myTicket);
        subject.executor().method("prices").execute();
//        subject.executor().method("parse").execute();


    }
}
