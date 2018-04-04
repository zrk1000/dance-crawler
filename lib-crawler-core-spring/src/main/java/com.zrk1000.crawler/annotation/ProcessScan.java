package com.zrk1000.crawler.annotation;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描指定包下Process注解的类
 * Created by rongkang on 2017/11/30.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ProcessScannerRegistrar.class)
public @interface ProcessScan {

    /**
     * 要扫描的包名 如：com.youyu
     * @return
     */
    String value() default "";

}
