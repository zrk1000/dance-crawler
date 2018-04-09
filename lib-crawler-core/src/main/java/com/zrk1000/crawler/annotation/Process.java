package com.zrk1000.crawler.annotation;

import java.lang.annotation.*;

/**
 * 自定义爬取业务处理器注解
 * Created by rongkang on 2017-08-20.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Process {

    String method();

    String[] domain();

}
