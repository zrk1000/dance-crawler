package com.zrk1000.crawler.processor;

/**
 * 为脚本提供的bean工厂，脚本中注入的服务会在此工厂中获取
 * User: zrk-PC
 * Date: 2017/9/11
 * Time: 15:30
 */
public interface ServiceFactory {

    Object getService(String beanName);

    <T> T getService(Class<T> beanType);
}
