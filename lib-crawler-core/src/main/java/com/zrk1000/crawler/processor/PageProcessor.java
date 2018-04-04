package com.zrk1000.crawler.processor;

import com.zrk1000.crawler.executor.Context;
import com.zrk1000.crawler.visitor.Visitor;

/**
 * 解析处理器接口
 * Created by rongkang on 2017-08-30.
 */
public interface PageProcessor {

    Object process(Visitor visitor, Context context);

}
