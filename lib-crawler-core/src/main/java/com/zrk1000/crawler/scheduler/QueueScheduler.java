package com.zrk1000.crawler.scheduler;


import com.zrk1000.crawler.visitor.http.HttpRequest;

import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 任务队列
 * User: zhouRongKang
 * Date: 2017/8/30
 * Time: 9:51
 */
public class QueueScheduler implements Scheduler {

    private Queue<HttpRequest> queue;

    private Set<String> used ;
    private Set<String> useful ;

    public QueueScheduler() {
        queue = new ConcurrentLinkedQueue();
        used = Collections.newSetFromMap(new ConcurrentHashMap<>());
        useful = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void push(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        if(!used.contains(url) && !useful.contains(url)){
            queue.add(httpRequest);
            useful.add(httpRequest.getUrl());
        }
    }

    @Override
    public HttpRequest poll() {
        HttpRequest request = queue.poll();
        if(request!=null){
            useful.remove(request.getUrl());
            used.add(request.getUrl());
        }
        return request;
    }

    @Override
    public int size() {
        return queue.size();
    }

}

    