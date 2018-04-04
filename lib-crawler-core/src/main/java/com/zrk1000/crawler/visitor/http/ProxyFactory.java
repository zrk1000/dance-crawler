package com.zrk1000.crawler.visitor.http;

import com.zrk1000.crawler.principal.Ticket;

/**
 * Created by rongkang on 2017-08-20.
 */
public interface ProxyFactory {

    Proxy getProxy(Ticket ticket);

}
