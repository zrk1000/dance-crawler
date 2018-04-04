package com.zrk1000.crawler.pipeline;

import com.zrk1000.crawler.executor.Context;

/**
 * 输出管道
 */
public interface Pipeline {

    void process(Context context);
}
