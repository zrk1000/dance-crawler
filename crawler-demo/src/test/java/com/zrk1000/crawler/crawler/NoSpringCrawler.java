package com.zrk1000.crawler.crawler;

import com.alibaba.fastjson.JSONObject;
import com.zrk1000.crawler.utils.ShowDialog;
import com.zrk1000.crawler.pipeline.ConsolePipeline;
import com.zrk1000.crawler.processor.GroovyProcessorRegister;
import com.zrk1000.crawler.processor.SimpleServiceFactory;
import com.zrk1000.crawler.subject.MySubject;
import com.zrk1000.crawler.ticket.MyTicket;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/9/1
 * Time: 11:48
 */
public class NoSpringCrawler {

    MyCrawler myCrawler;

    @Before
    public void before(){
//        String path = MyCrawler.class.getResource("/script").getPath();
        String classpath = MyCrawler.class.getResource("/").getFile();
        String path = classpath.substring(0, classpath.indexOf("target")) + "src/main/java/com.zrk1000/processor";
        //初始化 - groovy脚本目录
        File directory = new File(path);
        //初始化 - 创建爬虫对象
        myCrawler = new MyCrawler();
        //服务工厂
        SimpleServiceFactory serviceFactory = new SimpleServiceFactory();
        serviceFactory.registBean("aaa","I'm test bean");
        //初始化 - 设置处理器注册中心 ， 使用Groovy脚本注册中心
        myCrawler.setProcessorRegister(new GroovyProcessorRegister(directory,serviceFactory));
        //初始化 - 设置代理工厂
//        myCrawler.setProxyFactory(new MapProxyFactory());
    }

    @Test
    public void ankang0915() {
        //初始化 - 创建token 指定domain
        MyTicket ticket = new MyTicket();

        //初始化 - 创建subject
        MySubject subject = myCrawler.getSubject(ticket);

        //初始化 - 注册输出管道
        subject.addPipelines(new ConsolePipeline());

        //爬取 - 获取验证码
        String yzm = ShowDialog.img((byte[])subject.getCaptcha());

        //爬取 - 登陆参数
        ticket = (MyTicket) subject.getTicket();
        ticket.setCaptcha(yzm);
//        ticket.setRealName("高雁林");
//        ticket.setIdNo("420321198712085915");

        //爬取 - 登陆
        Object login = subject.login();

        //爬取 - 登陆结果
        System.out.println(login);
        //解析数据
        Object parse = subject.parse();
        //解析结果
        System.out.println(parse);
    }

    @Test
    public void shanghai021() {
        //初始化 - 创建token 指定domain
        MyTicket ticket = new MyTicket();

        //初始化 - 创建subject
        MySubject subject = myCrawler.getSubject(ticket);

        //初始化 - 注册输出管道
        subject.addPipelines(new ConsolePipeline());

        //爬取 - 获取验证码
        String yzm = ShowDialog.img((byte[])subject.getCaptcha());

        //爬取 - 登陆参数
        ticket = (MyTicket) subject.getTicket();
        ticket.setCaptcha(yzm);
        ticket.setUsername("zrk1000");
        ticket.setPassword("789320");

        //爬取 - 登陆
        Object login = subject.login();

        //爬取 - 登陆结果
        System.out.println(login);
        //解析数据
        Object parse = subject.parse();
        //解析结果
        System.out.println(parse);
    }

    /**
     * 同步顺序执行
     */
    @Test
    public void syncExecuteTest() {
        long start = System.currentTimeMillis();

        MyTicket ticket = new MyTicket();

        MySubject subject = myCrawler.getSubject(ticket);

//        subject.coverPipelines(new ConsolePipeline());

        Object method001 = subject.executor().method("method001").execute();
        Object method002 = subject.executor().method("method002").execute();
        Object method003 = subject.executor().method("method003").execute();
        Object method004 = subject.executor().method("method004").execute();
        Object method005 = subject.executor().method("method005").execute();

        System.out.println("method001:"+ method001);
        System.out.println("method002:"+ method002);
        System.out.println("method003:"+ method003);
        System.out.println("method004:"+ method004);
        System.out.println("method005:"+ method005);

        long end = System.currentTimeMillis();

        System.out.println("耗时：" + (end - start));
    }

    /**
     * 异步多线程执行 - 无返回值
     */
    @Test
    public void asynRunExecuteTest() throws InterruptedException {
        long start = System.currentTimeMillis();

        MyTicket ticket = new MyTicket();

        MySubject subject = myCrawler.getSubject(ticket);

//        String[] methods = new String[]{"method001"};
        String[] methods = new String[]{"method001", "method002", "method003", "method004", "method005"};

        subject.executor().method(methods).threadNum(6).runExecute();

        long end = System.currentTimeMillis();

        System.out.println("耗时：" + (end - start));

        TimeUnit.SECONDS.sleep(20);

    }

    /**
     * 异步多线程执行 - 有返回值
     */
    @Test
    public void asynCallExecuteTest() {
        long start = System.currentTimeMillis();

        MyTicket ticket = new MyTicket();

        MySubject subject = myCrawler.getSubject(ticket);

        String[] methods = new String[]{"method001", "method002", "method003", "method004", "method005"};

        Map<String, Object> resultMap = subject.executor().method(methods).threadNum(5).notSkipPipelings().callExecute();

        System.out.println(JSONObject.toJSONString(resultMap,true));

        long end = System.currentTimeMillis();

        System.out.println("耗时：" + (end - start));

    }



    @Test
    public void shanghai021Ergodic() {
        //初始化 - 创建token 指定domain
        MyTicket ticket = new MyTicket();

        //初始化 - 创建subject
        MySubject subject = myCrawler.getSubject(ticket);

        //初始化 - 注册输出管道
        subject.addPipelines(new ConsolePipeline());

        //爬取 - 获取验证码
        String yzm = ShowDialog.img((byte[])subject.getCaptcha());

        //爬取 - 登陆参数
        ticket = (MyTicket) subject.getTicket();
        ticket.setCaptcha(yzm);
        ticket.setUsername("zrk1000");
        ticket.setPassword("789320");

        //爬取 - 登陆
        Object login = subject.login();

        //爬取 - 登陆结果
        System.out.println(login);
        //遍历爬取数据
        Object parse = subject.executor().method("ergodic").execute();
        //解析结果
        System.out.println(parse);
    }
}

    