package com.zrk1000.crawler;


import com.zrk1000.crawler.pipeline.ConsolePipeline;
import com.zrk1000.crawler.pipeline.Pipeline;
import com.zrk1000.crawler.principal.Subject;
import com.zrk1000.crawler.principal.Ticket;
import com.zrk1000.crawler.processor.ProcessorRegister;
import com.zrk1000.crawler.processor.SimpleProcessorRegister;
import com.zrk1000.crawler.processor.SimpleServiceFactory;
import com.zrk1000.crawler.session.SessionManager;
import com.zrk1000.crawler.session.SimpleSessionManager;
import com.zrk1000.crawler.visitor.SimpleVisitorGenerator;
import com.zrk1000.crawler.visitor.VisitorGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhouRongKang
 * Date: 2017/8/21
 * Time: 18:19
 */
public abstract class SimpleCrawler<S extends Subject,T extends Ticket> implements Crawler<S, T> {

    protected ProcessorRegister processorRegister;
    protected SessionManager sessionManager;
    protected VisitorGenerator visitorGenerator;
    protected List<Pipeline> pipelines;

    public SimpleCrawler() {
        this.sessionManager = new SimpleSessionManager();
        this.visitorGenerator = new SimpleVisitorGenerator();
        this.pipelines = new ArrayList(){{add(new ConsolePipeline());}};
    }

    public SimpleCrawler(String processorPackage) {
        this();
        this.processorRegister = new SimpleProcessorRegister(processorPackage,new SimpleServiceFactory());

    }

    public SimpleCrawler(ProcessorRegister processorRegister,SessionManager sessionManager, VisitorGenerator visitorGenerator,List<Pipeline> pipelines) {
        this.processorRegister = processorRegister;
        this.sessionManager = sessionManager;
        this.visitorGenerator = visitorGenerator;
        this.pipelines = pipelines;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    public ProcessorRegister getProcessorRegister() {
        return processorRegister;
    }

    public void setProcessorRegister(ProcessorRegister processorRegister) {
        this.processorRegister = processorRegister;
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    public VisitorGenerator getVisitorGenerator() {
        return visitorGenerator;
    }

    public void setVisitorGenerator(VisitorGenerator visitorGenerator) {
        this.visitorGenerator = visitorGenerator;
    }
}

    