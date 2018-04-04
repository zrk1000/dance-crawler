package com.zrk1000.crawler.processor;

import com.zrk1000.crawler.executor.Context;
import com.zrk1000.crawler.scheduler.QueueScheduler;
import com.zrk1000.crawler.scheduler.Scheduler;
import com.zrk1000.crawler.visitor.Visitor;
import com.zrk1000.crawler.visitor.http.HttpRequest;
import com.zrk1000.crawler.visitor.http.HttpResponse;
import com.zrk1000.crawler.visitor.http.Page;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 遍历页面爬取处理器
 * User: zhouRongKang
 * Date: 2017/8/30
 * Time: 9:44
 */
public abstract class ErgodicPageProcessor implements PageProcessor {

    private Logger logger = LoggerFactory.getLogger(ErgodicPageProcessor.class);

    private Scheduler scheduler;

    public ErgodicPageProcessor() {
        this.scheduler = new QueueScheduler();
    }


    @Override
    public Object process(Visitor visitor, Context context) {
        init(visitor,context,scheduler);
        HttpRequest request = null;
        while ((request = scheduler.poll()) !=null) {
            try {
                logger.debug("request url:{}",request.getUrl());
                logger.debug("request deep:{}",request.getDeep());
                logger.debug("scheduler size:{}",scheduler.size());
                processRequest(visitor,context,request);
            } catch (Exception e) {
                logger.error("processor request " + request + " error", e);
            }
            sleep(interval());
        }
        return null;
    }

    /**
     * 初始化操作 此方法需要提供种子url，才可使爬虫启动遍历爬取
     * @param visitor
     * @param context
     * @return
     */
    public abstract void init(Visitor visitor,Context context,Scheduler scheduler);

    /**
     * 爬取业务 抽取子页面链接等操作
     * @param page
     * @param context
     * @return
     */
    public abstract Object process(Page page, Context context);

    /**
     * 请求间隔
     * @return
     */
    public int interval(){
        return 1000;
    }

    /**
     * 遍历深度
     * @return
     */
    public int deep(){
        return 4;
    }


    private void processRequest(Visitor visitor, Context context,HttpRequest request) {
        HttpResponse httpResponse = visitor.visit(request);
        Page page = httpResponse.page();
        if (page.code()==200){
            onVisitorSuccess(page,context);
        } else {
            onVisitorFail(request);
        }
    }

    private void onVisitorSuccess( Page page, Context context) {
        process(page,context);
        addRequestsToScheduler(page.getHttpRequests());
    }

    private void onVisitorFail(HttpRequest request) {
            // for cycle retry
//        doCycleRetry(request);
    }

    public void addRequestsToScheduler(List<HttpRequest> requestList) {
        if (CollectionUtils.isNotEmpty(requestList)) {
            for (HttpRequest request : requestList) {
                if(request.getDeep() < deep()){
                    request.setDeep(request.getDeep()+1);
                    scheduler.push(request);
                }
            }
        }
    }

//    public void addRequestToScheduler(HttpRequest request) {
//        scheduler.push(request);
//    }


    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted when sleep",e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


}

