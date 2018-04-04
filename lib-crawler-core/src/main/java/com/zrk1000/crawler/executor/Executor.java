package com.zrk1000.crawler.executor;

import com.zrk1000.crawler.exception.CrawlerException;
import com.zrk1000.crawler.pipeline.Pipeline;
import com.zrk1000.crawler.processor.PageProcessor;
import com.zrk1000.crawler.visitor.Visitor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 爬虫执行器
 * User: zhouRongKang
 * Date: 2017/8/31
 * Time: 10:25
 */
public class Executor {

    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

    /**
     * 线程池 processor执行时从此线程池获取线程资源
     */
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 处理器集合
     */
    private Map<String,PageProcessor> processors = new HashMap();

    /**
     * 网络访问器
     */
    private Visitor visitor;

    /**
     * 上下文 用于同一个Executor的多个processor执行期间数据传递
     *
     */
    private Context context;

    /**
     * 输出管道 processor执行后会依次执行pipelines
     */
    private List<Pipeline> pipelines;

    public Executor(Map<String,PageProcessor> processors, Visitor visitor, Context context) {
        this(processors, visitor, context, new ArrayList());
    }

    public Executor(Map<String,PageProcessor> processors, Visitor visitor, Context context,List<Pipeline> pipelines) {
        this.processors = processors;
        this.visitor = visitor;
        this.context = context;
        this.pipelines = pipelines==null?new ArrayList():pipelines;
    }

    /**
     * 依次执行pipelines
     */
    private void doPipeline(){
        if(!CollectionUtils.isEmpty(pipelines)&&!context.isSkipPipelings()){
            for (Pipeline pipeline : pipelines){
                pipeline.process(context);
            }
        }
    }

    /**
     * 执行处理器
     * @param method
     * @return
     */
    private Object doProcessor(String method){
        if(method == null) {
            throw CrawlerException.newInstance(653, "Parameter methods must not be empty");
        }
        PageProcessor pageProcessor = processors.get(method);
        if(pageProcessor!=null){
            return pageProcessor.process(visitor, context);
        }else {
            throw CrawlerException.newInstance(654,"processor %s.%s not exist",context.getTicket().getDomain(),method);
        }
    }

    /**
     * 同步执行 单次只可执行单个processor  processor执行后会依次执行pipelines
     * @param method
     * @return
     */
    public Object execute(String method){
        Object result = doProcessor(method);
        doPipeline();
        return result;
    }

    /**
     * 异步执行 单次可执行多个processor 无返回结果  processor执行后会依次执行pipelines
     * @param threadNum
     * @param skipPipelings
     * @param methods
     * @return
     */
    public void runExecute(int threadNum,boolean skipPipelings,String... methods){
        if(methods == null || methods.length == 0) {
            throw CrawlerException.newInstance(653, "Parameter methods must not be empty");
        }
        CountDownLatch latch = new CountDownLatch(methods.length);
        CountableThreadPool threadPool = new CountableThreadPool(threadNum,executorService);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (String method: methods) {
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                doProcessor(method);
                            }finally {
                                latch.countDown();
                            }
                        }
                    });
                }
                try {
                    latch.await();
                    if(!skipPipelings) {
                        doPipeline();
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(),e);
                }

            }
        });
    }

    /**
     * 异步执行 单次可执行多个processor 统一返回结果 processor执行后会依次执行pipelines
     * @param threadNum     执行此次processors的线程数
     * @param skipPipelings processors执行结束后是否跳过pipelines
     * @param methods       processor method
     * @return
     */
    public Map<String,Object> callExecute(int threadNum,boolean skipPipelings,String... methods){
        if(methods == null || methods.length == 0) {
            throw CrawlerException.newInstance(653, "Parameter methods must not be empty");
        }
        Map<String,Object> results = new ConcurrentHashMap();
        CountDownLatch latch = new CountDownLatch(methods.length);
        CountableThreadPool threadPool = new CountableThreadPool(threadNum,executorService);
        for (String method: methods) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        Object result = doProcessor(method);
                        results.put(method,result);
                    }finally {
                        latch.countDown();
                    }
                }
            });
        }
        try {
            latch.await();
            if(!skipPipelings) {
                doPipeline();
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }
        return results;
    }

    private void asyncExecute(){

    }
    /**
     * 添加输出管道
     * @param pipelineArr
     */
    public void addPipelines(Pipeline... pipelineArr){
        if(this.pipelines == null) {
            this.pipelines = new ArrayList();
        }
        if(pipelineArr!=null) {
            for (Pipeline pineline : pipelineArr) {
                pipelines.add(pineline);
            }
        }

    }

    /**
     *  覆盖替换输出管道
     * @param pipelineArr
     */
    public void coverPipelines(Pipeline... pipelineArr){
        if(this.pipelines != null) {
            this.pipelines.clear();
        }
        addPipelines(pipelineArr);
    }

    public Map<String, PageProcessor> getProcessors() {
        return processors;
    }

    public void setProcessors(Map<String, PageProcessor> processors) {
        this.processors = processors;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

}

    