package com.zrk1000.crawler;

import com.zrk1000.crawler.principal.Subject;
import com.zrk1000.crawler.principal.Ticket;

/**
 * Created with IntelliJ IDEA.
 * User: zhouRongKang
 * Date: 2017/8/31
 * Time: 12:39
 */
public interface Crawler<S extends Subject,T extends Ticket> {

     S getSubject(T token);
}
