package com.zrk1000.crawler.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * 为脚本提供的bean工厂，脚本中注入的服务会在此工厂中获取
 * User: zrk-PC
 * Date: 2017/9/11
 * Time: 15:35
 */
public class SimpleServiceFactory implements ServiceFactory {

    private Map<Object,Object> serviceMap;

    public SimpleServiceFactory() {
        this.serviceMap = new HashMap();
    }

    @Override
    public Object getService(String beanName) {
        return serviceMap.get(beanName);
    }

    @Override
    public <T> T getService(Class<T> beanType) {
        Object o = serviceMap.get(beanType);
        return o != null ? (T)o : null;
    }

    public void registBean(String beanName,Object bean){
        if(beanName == null || "".equals(beanName.trim()) || bean == null){
            throw new RuntimeException("Parameter beanName or bean cannot be empty");
        }
        if(serviceMap.containsKey(beanName)){
            throw new RuntimeException("Bean "+beanName+" already exists");
        }
        serviceMap.put(beanName,bean);
        serviceMap.put(bean.getClass(),bean);
    }



}

    