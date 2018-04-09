package com.zrk1000.crawler.pipeline;

import com.zrk1000.crawler.executor.Context;

/**
 * 输出管道接口
 * User: zhouRongKang
 * Date: 2017/8/31
 * Time: 10:25
 */
public interface Pipeline {

    void process(Context context);
}
