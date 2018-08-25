package com.zrk1000.crawler;

import com.zrk1000.crawler.annotation.ProcessScan;
import com.zrk1000.crawler.crawler.MyCrawler;
import com.zrk1000.crawler.pipeline.ConsolePipeline;
import com.zrk1000.crawler.pipeline.Pipeline;
import com.zrk1000.crawler.processor.ProcessorRegister;
import com.zrk1000.crawler.processor.ServiceFactory;
import com.zrk1000.crawler.processor.SpringGroovyProcessorRegister;
import com.zrk1000.crawler.processor.SpringServiceFactory;
import com.zrk1000.crawler.session.*;
import com.zrk1000.crawler.visitor.SimpleVisitorGenerator;
import com.zrk1000.crawler.visitor.VisitorGenerator;
import com.zrk1000.crawler.visitor.http.ProxyFactory;
import com.zrk1000.crawler.visitor.okhttp.OKHttpPool;
import com.zrk1000.crawler.visitor.okhttp.OkHttpLogger;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * bean配置
 * User: zhouRongKang
 * Date: 2017/8/21
 * Time: 18:29
 */
@Configuration
@ProcessScan("com.zrk1000.crawler")
public class BeanConfiguration {

    private static Logger logger = LoggerFactory.getLogger(BeanConfiguration.class);

    @Autowired
    private Environment environment;

    /**
     * 会话持久实现类 redis实现
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public SessionDao redisSessionDao(RedisTemplate redisTemplate) {
//        RedisSessionDao sessionDao = new RedisSessionDao(redisTemplate);
        MemorySessionDao sessionDao = new MemorySessionDao();
        return sessionDao;
    }

    /**
     * session 管理器 默认使用内存版会话持久实现
     *
     * @param sessionDao
     * @return
     */
    @Bean
    public SessionManager sessionManager(SessionDao sessionDao) {
        Long timeout = 5 * 60 * 1000L;
        SimpleSessionManager.DEFAULT_SESSION_TIMEOUT = timeout;
        SimpleSessionManager simpleSessionManager = new SimpleSessionManager();
        simpleSessionManager.setSessionDao(sessionDao);
        simpleSessionManager.setTimout(timeout);
        simpleSessionManager.setEnableVlidationScheduler(false);
        return simpleSessionManager;
    }

    /**
     * 网络访问层连接池
     *
     * @return
     */
    @Bean
    public OKHttpPool okHttpPool() {
        OKHttpPool httpPool = new OKHttpPool(5000, 60);
        return httpPool;
    }

    /**
     * okhttp 日志拦截器
     *
     * @return
     */
    @Profile({"dev", "test"})
    @Bean
    public Interceptor networkInterceptor() {
//        return new HttpLoggingInterceptor(new OkHttpLogger()).setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return new HttpLoggingInterceptor(new OkHttpLogger()).setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    /**
     * okhttp 日志拦截器
     *
     * @return
     */
    @Profile("prod")
    @Bean
    public Interceptor networkInterceptorProd() {
        return new HttpLoggingInterceptor(new OkHttpLogger()).setLevel(HttpLoggingInterceptor.Level.NONE);
    }


    /**
     * Visitor生成器 保证每个subject初始化都能获取唯一的Visitor
     *
     * @param okHttpPool
     * @param proxyFactory
     * @param networkInterceptor
     * @return
     */
    @Bean
    public VisitorGenerator visitorGenerator(OKHttpPool okHttpPool, ProxyFactory proxyFactory, Interceptor networkInterceptor) {
        SimpleVisitorGenerator visitorGenerator = new SimpleVisitorGenerator();
        visitorGenerator.setOkHttpPool(okHttpPool);
        visitorGenerator.setProxyFactory(proxyFactory);
        visitorGenerator.setNetworkInterceptors(new ArrayList() {{
            add(networkInterceptor);
        }});
        return visitorGenerator;
    }

    /**
     * SpringServiceFactory 用作在spring环境下，会将所有spring bean 当做服务
     * SimpleServiceFactory 用作在非spring环境下 ，服务需要手动初始
     *
     * @return
     */
    @Bean
    public ServiceFactory serviceFactory() {
        ServiceFactory serviceFactory = new SpringServiceFactory();
//        ServiceFactory serviceFactory = new SimpleServiceFactory();
        return serviceFactory;
    }

    /**
     * 业务逻辑处理器注册中心
     * SpringProcessorRegister：spring环境下的自动扫描注册中心
     * SimpleProcessorRegister：基于自定义注解自动扫描处理器的注册中心
     * GroovyProcessorRegister：Groovy脚本动态加载注册中心
     *
     * @return
     */
    @Profile("dev")
    @Bean
    public ProcessorRegister processorRegisterDev(ServiceFactory serviceFactory) {
//        ProcessorRegister processorRegister = new SimpleProcessorRegister("com.youyu");
//        ProcessorRegister processorRegister = new SpringProcessorRegister();
//        初始化 - groovy脚本目录
        String classpath = MyCrawler.class.getResource("/").getFile();
        String path = classpath.substring(0, classpath.indexOf("target")) + "/src/main/java/com.zrk1000/processor";
        File directory = new File(path);
        //过滤掉的文件
        NotFileFilter notFileFilter = new NotFileFilter(new NameFileFilter(new String[]{"SiBaseProcessor.java"}));
        SpringGroovyProcessorRegister processorRegister = new SpringGroovyProcessorRegister(directory, notFileFilter, serviceFactory, 0, 5000L);
        return processorRegister;
    }

    @Profile(value = {"test", "prod"})
    @Bean
    public ProcessorRegister processorRegister(ServiceFactory serviceFactory) {
        //初始化 - groovy脚本目录
        String processorScriptPath = environment.getProperty("processor.script.path");
        logger.info("load script path:{}", processorScriptPath);
        File directory = new File(processorScriptPath);
        NotFileFilter notFileFilter = new NotFileFilter(new NameFileFilter(new String[]{"SiBaseProcessor.java"}));
        ProcessorRegister processorRegister = new SpringGroovyProcessorRegister(directory, notFileFilter, serviceFactory, 0, 1 * 60 * 1000L);
        return processorRegister;
    }


//    @Bean
//    @Order(2)
//    public Pipeline databasePipeline() {
//        return new DatabasePipeline();
//    }

    @Bean
    @Order(1)
    @Profile("dev")
    public Pipeline consolePipeline() {
        return new ConsolePipeline();
    }

    /**
     * 爬虫实例
     *
     * @param processorRegister
     * @param sessionManager
     * @param visitorGenerator
     * @return
     */
    @Bean
    @Scope("prototype")
    public MyCrawler crawler(ProcessorRegister processorRegister, SessionManager sessionManager, VisitorGenerator visitorGenerator, List<Pipeline> pipelines) {
        MyCrawler crawler = new MyCrawler();
        crawler.setProcessorRegister(processorRegister);
        crawler.setSessionManager(sessionManager);
        crawler.setVisitorGenerator(visitorGenerator);
        crawler.setPipelines(pipelines);
        return crawler;
    }


}

    