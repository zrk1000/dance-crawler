package com.zrk1000.crawler.proxy;


import com.zrk1000.crawler.principal.Ticket;
import com.zrk1000.crawler.ticket.MyTicket;
import com.zrk1000.crawler.visitor.http.Proxy;
import com.zrk1000.crawler.visitor.http.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理工厂map简单实现 测试使用
 * Created by rongkang on 2017-09-03.
 */
@Component
public class MapProxyFactory implements ProxyFactory {

    private static Logger logger = LoggerFactory.getLogger(MapProxyFactory.class);


    private static Map<String,Proxy> proxyMap = new HashMap(){{
        put("0916",new Proxy("60.169.78.218",808));
        put("0915",new Proxy("119.109.99.227",8118));
        put("021",new Proxy("61.135.217.7",80));
    }};

    @Override
    public Proxy getProxy(Ticket ticket) {
        MyTicket myTicket = (MyTicket) ticket;
        Proxy proxy = proxyMap.get(myTicket.getDomain());
        if(proxy == null) {
            proxy = proxyMap.get("default");
        }
        logger.debug("load proxy:"+ proxy);
        return proxy;
    }
}
