package com.zrk1000.crawler.visitor;

import com.zrk1000.crawler.exception.CrawlerException;
import com.zrk1000.crawler.visitor.http.ProxyFactory;
import com.zrk1000.crawler.visitor.okhttp.OKHttpPool;
import okhttp3.Interceptor;

import java.util.List;

/**
 * 访问器的构造器
 * @author zhouRongKang
 * @date 2017/11/10 18:48
 */
public class SimpleVisitorGenerator implements VisitorGenerator {

    private OKHttpPool okHttpPool;

    private ProxyFactory proxyFactory;

    private List<Interceptor> interceptors;

    private List<Interceptor> networkInterceptors;

    public SimpleVisitorGenerator() {
        this.okHttpPool = new OKHttpPool();
    }

    public SimpleVisitorGenerator(OKHttpPool okHttpPool, ProxyFactory proxyFactory) {
        this.okHttpPool = okHttpPool;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Visitor generate() {
        if (okHttpPool == null) {
            throw CrawlerException.newInstance(657, "The 'httpPool' parameter cannot be empty!");
        }
        OkHttpVisitor visitor = new OkHttpVisitor(okHttpPool);
        if (proxyFactory != null) {
            visitor.setProxyFactory(proxyFactory);
        }
        if (interceptors != null) {
            visitor.setInterceptors(interceptors);
        }
        if (networkInterceptors != null) {
            visitor.setNetworkInterceptors(networkInterceptors);
        }
        return visitor;
    }

    public OKHttpPool getOkHttpPool() {
        return okHttpPool;
    }

    public void setOkHttpPool(OKHttpPool okHttpPool) {
        this.okHttpPool = okHttpPool;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public List<Interceptor> getNetworkInterceptors() {
        return networkInterceptors;
    }

    public void setNetworkInterceptors(List<Interceptor> networkInterceptors) {
        this.networkInterceptors = networkInterceptors;
    }
}
