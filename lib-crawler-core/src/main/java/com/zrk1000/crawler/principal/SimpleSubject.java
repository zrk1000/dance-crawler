package com.zrk1000.crawler.principal;

import com.zrk1000.crawler.exception.CrawlerException;
import com.zrk1000.crawler.executor.Context;
import com.zrk1000.crawler.executor.Executor;
import com.zrk1000.crawler.pipeline.Pipeline;
import com.zrk1000.crawler.processor.PageProcessor;
import com.zrk1000.crawler.processor.ProcessorRegister;
import com.zrk1000.crawler.session.Session;
import com.zrk1000.crawler.session.SessionAware;
import com.zrk1000.crawler.session.SessionManager;
import com.zrk1000.crawler.visitor.Visitor;

import java.util.List;
import java.util.Map;

/**
 * 访问者基础实现
 * Created by rongkang on 2017-08-20.
 */
public class SimpleSubject implements Subject {

    /**
     * 会话管理器
     */
    private SessionManager sessionManager;
    /**
     * 执行器
     */
    private Executor executor;
    /**
     * 访问者token
     */
    private Ticket ticket;
    /**
     * 上下文
     */
    private Context context;
    /**
     * 访问器
     */
    private Visitor visitor;

    public SimpleSubject(Ticket ticket, ProcessorRegister processorRegister, SessionManager sessionManager, Visitor visitor, List<Pipeline> pipelines) {
        this.ticket = ticket;
        this.sessionManager = sessionManager;
        this.visitor = visitor;
        init(processorRegister,pipelines);
    }

    /**
     * 初始化处理器注册工厂、代理工厂、访问器、上下文等
     * 若注册了代理工厂，对访问器设置代理，默认使用session中有代理，
     * 若初次创建或session中无代理，则调用代理工厂获取，代理工厂可返回空代理，即不使用代理
     * @param processorRegister
     * @param pipelines
     */
    void init(ProcessorRegister processorRegister,List<Pipeline> pipelines){
        if(ticket.getDomain() == null){
            throw CrawlerException.newInstance(656,"The 'domain' parameter cannot be empty!");
        }
        applySessionAware();
        applyTicketAware();
        initContext();
        initExecutor(processorRegister, pipelines);
    }
    private void initContext(){
        this.context = new Context.Builder().subject(this).token(ticket).build();
    }

    private void initExecutor(ProcessorRegister processorRegister,List<Pipeline> pipelines){
        this.executor = new Executor(initProcess(processorRegister),visitor,context,pipelines);
    }
    /**
     * 初始化处理器集合
     * 从处理器注册中心获取当前domain下注册的处理器
     * @param processorRegister
     * @return
     */
    private Map<String,PageProcessor> initProcess(ProcessorRegister processorRegister){
        Map<String,PageProcessor> processors = processorRegister.load(ticket.getDomain());
        if(processors == null){
            throw CrawlerException.newInstance(655,"The processor doesn't exist! domain : %s" , ticket.getDomain());
        }
        return processors;
    }

    /**
     * 使用Aware模式给访问器注入session
     */
    private void applySessionAware() {
        if (this.visitor != null && this.visitor instanceof SessionAware){
            ((SessionAware) this.visitor).setSession(getSession());
        }
    }

    /**
     * 使用Aware模式给访问器注入ticket
     */
    private void applyTicketAware() {
        if (this.visitor != null && this.visitor instanceof TicketAware){
            ((TicketAware) this.visitor).setTicket(ticket);
        }
    }




    /**
     * 获取session并设置token的identify
     * @return
     */
    @Override
    public Session getSession() {
        Session session = sessionManager.getSession(ticket.getIdentify());
        ticket.setIdentify(session.getId());
        return session;
    }

    @Override
    public SubjectExecutor executor() {
        return new SubjectExecutor(this.executor);
    }

    /**
     * 添加内容输出管道
     * 如将爬取内容存入数据库等
     * @param pipelineArr
     */
    public void addPipelines(Pipeline... pipelineArr){
        executor.addPipelines(pipelineArr);
    }
    /**
     * 替换内容输出管道
     * 如将爬取内容存入数据库等
     * @param pipelineArr
     */
    public void coverPipelines(Pipeline... pipelineArr){
        executor.coverPipelines(pipelineArr);
    }


    @Override
    public Ticket getTicket() {
        return ticket;
    }

    public Context getContext() {
        return context;
    }
}
