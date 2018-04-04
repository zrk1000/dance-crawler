package com.zrk1000.crawler.visitor.okhttp;

import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * okhttp 请求日志拦截器
 * @author zhouRongKang
 * @date 2017/12/12 11:05
 */
public class OkHttpLogger implements HttpLoggingInterceptor.Logger{
    private static Logger logger = LoggerFactory.getLogger(OkHttpLogger.class);

    @Override
    public void log(String message) {
        if(logger.isDebugEnabled()){
            System.out.println(message);
        }
    }
}
