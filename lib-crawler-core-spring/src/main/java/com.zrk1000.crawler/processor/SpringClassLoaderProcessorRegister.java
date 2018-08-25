package com.zrk1000.crawler.processor;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;

/**
 * 处理器注册中心
 * 扫描指定目录下java源码，用于支持动态部署 ， 使用定时任务刷新脚本
 * 重写ClassLoaderProcessorRegister的doInject()方法 ，添加对spring的@Autowired注解的支持
 * User: zrk-PC
 * Date: 2018/5/29
 * Time: 12:21
 */
public class SpringClassLoaderProcessorRegister extends ClassLoaderProcessorRegister {

    private static Logger logger = LoggerFactory.getLogger(SpringClassLoaderProcessorRegister.class);

    public SpringClassLoaderProcessorRegister(File directory, ServiceFactory serviceFactory) {
        super(directory, serviceFactory);
    }

    public SpringClassLoaderProcessorRegister(File directory, IOFileFilter fileFilter, ServiceFactory serviceFactory, int delay, long period) {
        super(directory, fileFilter ,serviceFactory,delay, period);
    }
    @Override
    protected void doInject(Object instance) {
        if( getServiceFactory() == null){
            logger.info("The ServiceFactory is empty");
            return;
        }
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object fieldBean = null;
            Resource resource = AnnotationUtils.getAnnotation(field,Resource.class);
            Autowired autowired = AnnotationUtils.getAnnotation(field,Autowired.class);
            if(resource!=null){
                if (resource.name()!=null && resource.name().length()>0){
                    fieldBean = getServiceFactory().getService(resource.name());
                } else {
                    fieldBean = getServiceFactory().getService(field.getName());
                }
                if (fieldBean==null ) {
                    fieldBean = getServiceFactory().getService(field.getType());
                }
            }else if(autowired != null){
                Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
                if (qualifier!=null && qualifier.value()!=null && qualifier.value().length()>0) {
                    fieldBean = getServiceFactory().getService(qualifier.value());
                } else {
                    fieldBean = getServiceFactory().getService(field.getType());
                }
                if (fieldBean==null ) {
                    fieldBean = getServiceFactory().getService(field.getName());
                }
            }
            if (fieldBean!=null) {
                //获取访问权限
                field.setAccessible(true);
                try {
                    field.set(instance, fieldBean);
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(),e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(),e);
                }
            }else {
//                logger.debug("Not found the bean :" + field.getName() +"");
            }
        }
    }
}

    