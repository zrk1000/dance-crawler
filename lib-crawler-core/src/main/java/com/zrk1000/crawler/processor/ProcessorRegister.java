package com.zrk1000.crawler.processor;

import java.util.Map;

/**
 * 处理器注册中心接口
 * User: zhouRongKang
 * Date: 2017/8/31
 * Time: 9:56
 */
public interface ProcessorRegister {

    /**
     * 注册处理器
     * @param domain
     * @param method
     * @param processor
     * @return
     */
    Map<String,PageProcessor> regist(String domain, String method, PageProcessor processor);

    /**
     * 获取处理器
     * @param domain
     * @return
     */
    Map<String,PageProcessor> load(String domain);

    /**
     * 获取bean工厂，bean工厂中的服务可以被处理器通过@Resource注解直接注入
     * @return
     */
    ServiceFactory getServiceFactory();
}

    