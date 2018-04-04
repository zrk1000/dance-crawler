package com.zrk1000.crawler.visitor;


import com.zrk1000.crawler.visitor.http.HttpRequest;
import com.zrk1000.crawler.visitor.http.HttpResponse;

/**
 * 网络访问器 默认使用setProxy注入的代理 若HttpRequest单独设置了Proxy,则此单独设置的Proxy仅对本次访问有效
 * User: zhouRongKang
 * Date: 2017/8/28
 * Time: 16:28
 */
public interface Visitor {

    /**
     * 网络请求主方法
     * @param request
     * @return
     */
    HttpResponse visit(HttpRequest request);

    /**
     * 是否允许自动重定向，默认为true
     * @param allow
     */
    void setAutoRedirect(boolean allow);

    /**
     * 设置超时时间 单位：秒
     * @param connectTimeout    连接超时
     * @param readTimeout       读超时
     * @param writeTimeout      写超时
     */
    void setTimeOut(Integer connectTimeout, Integer readTimeout, Integer writeTimeout);

    /**
     * 清除会话
     * @return
     */
    void closeSession();



}
