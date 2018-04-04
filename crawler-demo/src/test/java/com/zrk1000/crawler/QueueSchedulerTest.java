package com.zrk1000.crawler;

import com.zrk1000.crawler.scheduler.QueueScheduler;
import com.zrk1000.crawler.visitor.http.HttpRequest;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/9/14
 * Time: 16:31
 */
public class QueueSchedulerTest {

    @Test
    public void test(){
        QueueScheduler queueScheduler = new QueueScheduler();
        queueScheduler.push(HttpRequest.newBuilder().get("111").build());
        queueScheduler.push(HttpRequest.newBuilder().get("222").build());
        queueScheduler.push(HttpRequest.newBuilder().get("333").build());
        queueScheduler.push(HttpRequest.newBuilder().get("444").build());
        for (int i = 0 ;i < 5 ; i ++){
            HttpRequest poll = queueScheduler.poll();
            System.out.println(poll);
        }
    }
}

    