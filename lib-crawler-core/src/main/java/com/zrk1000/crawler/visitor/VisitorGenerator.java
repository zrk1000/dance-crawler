package com.zrk1000.crawler.visitor;

/**
 * 访问器持有者
 * @author zhouRongKang
 * @date 2017/11/10 18:46
 */
public interface VisitorGenerator {

    /**
     * 构造一个访问器
     * @return
     */
    Visitor generate();

}
