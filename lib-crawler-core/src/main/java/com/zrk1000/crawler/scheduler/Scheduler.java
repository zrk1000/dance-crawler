package com.zrk1000.crawler.scheduler;


import com.zrk1000.crawler.visitor.http.HttpRequest;

public interface Scheduler {

    public void push(HttpRequest httpRequest);

    public HttpRequest poll();

    public int size();

}
