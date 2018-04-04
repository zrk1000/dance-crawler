package com.zrk1000.crawler.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.zrk1000.crawler.annotation.Process;

import java.util.Map;

/**
 *  基于spring的处理器注册中心
 *  此注册中心用作注册打包在项目中的处理器 ,
 *  配合@ProcessScan使用 ,会将所有@Process注解的类注册到处理器注册中心 -  2017-11-30
 * Created by rongkang on 2017-08-20.
 */
public class SpringProcessorRegister extends AbstractProcessorRegister implements ApplicationContextAware{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static ApplicationContext applicationContext;

    @Override
    public ServiceFactory getServiceFactory() {
        return null;
    }


    private void initProcessRepository(){
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(Process.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof PageProcessor){
                    Process annotation = serviceBean.getClass().getAnnotation(Process.class);
                    for (String domain: annotation.domain()) {
                        regist(domain, annotation.method(), (PageProcessor) serviceBean);
                    }
                }
            }
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringProcessorRegister.applicationContext = applicationContext;
        initProcessRepository();
    }

}
