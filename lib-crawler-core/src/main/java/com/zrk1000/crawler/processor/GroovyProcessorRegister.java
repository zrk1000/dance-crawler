package com.zrk1000.crawler.processor;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.codehaus.groovy.reflection.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zrk1000.crawler.annotation.Process;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * 处理器注册中心
 * 扫描制定目录下Groovy脚本，用于支持动态部署 ， 使用定时任务刷新脚本
 * <p>
 * 未来目标：
 * 1、支持通过jmx监控注册中心 pass
 * 2、支持多种方式触发脚本刷新 no
 * 3、支持多种方式加载脚本    no
 * 4、支持脚本内引用对象注入  ok
 * <p>
 * 存储格式：Map<domain,Map<method,PageProcessor>>
 * User: zhouRongKang
 * Date: 2017/9/5
 * Time: 10:22
 */
public class GroovyProcessorRegister extends AbstractProcessorRegister {

    private static Logger logger = LoggerFactory.getLogger(GroovyProcessorRegister.class);

    public static final int DEFAULT_PROCESSOR_SCAN_DELAY = 10 * 1000;
    public static final long DEFAULT_PROCESSOR_SCAN_PERIOD = 60 * 1000;

    /**
     * 服务工厂
     */
    private ServiceFactory serviceFactory;

    /**
     * 要扫描的目录
     */
    private File directory;
    /**
     * 扫描排除规则
     */
    private IOFileFilter fileFilter;

    /**
     * 用于加载脚本的classLoader
     */
    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader(getClass().getClassLoader());

    /**
     * 扫描文件定时器 首次等待 时间
     */
    private long delay;

    /**
     * 扫描文件定时器 间隔 时间
     */
    private long period;

    /**
     * 扫描的文件的编辑时间
     */
    private Map<String, Long> referenceMap = new HashMap();

    public GroovyProcessorRegister(File directory, ServiceFactory serviceFactory) {
        this(directory, null, serviceFactory, DEFAULT_PROCESSOR_SCAN_DELAY, DEFAULT_PROCESSOR_SCAN_PERIOD);
    }

    public GroovyProcessorRegister(File directory, IOFileFilter fileFilter, ServiceFactory serviceFactory, Integer delay, Long period) {
        this.directory = directory;
        this.fileFilter = fileFilter;
        this.serviceFactory = serviceFactory;
        this.delay = delay;
        this.period = period;
        scan();
        task();
    }

    /**
     * 定时刷新脚本
     */
    public void task() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                scan();
            }
        }, delay, period);
    }

    /**
     * 扫描加载脚本
     */
    public void scan() {
        long start = System.currentTimeMillis();
        int count = 0;
        IOFileFilter filter = FileFilterUtils.and(
                EmptyFileFilter.NOT_EMPTY,
                new SuffixFileFilter(new String[]{"groovy", "java"}),
                new AbstractFileFilter() {
                    @Override
                    public boolean accept(final File file) {
                        Long lastModified = referenceMap.get(file.getAbsolutePath());
                        if (lastModified != null && lastModified.longValue() == file.lastModified()) {
                            return false;
                        }
                        return true;
                    }
                }
        );
        if (this.fileFilter != null) {
            filter = FileFilterUtils.and(filter, fileFilter);
        }
        Collection<File> files = FileUtils.listFiles(directory, filter, DirectoryFileFilter.INSTANCE);
        for (File file : files) {
            try {
                referenceMap.put(file.getAbsolutePath(), file.lastModified());
                Class<?> clazz = groovyClassLoader.parseClass(file);
                Object instance = clazz.newInstance();
                if (instance != null) {
                    if (instance instanceof PageProcessor) {
                        Process annotation = clazz.getAnnotation(Process.class);
                        if(annotation == null){
                            continue;
                        }
                        doInject(instance);
                        for (String domain: annotation.domain()) {
                            regist(domain, annotation.method(), (PageProcessor) instance);
                        }
                        if (logger.isDebugEnabled()) {
                            List<String> lines = null;
                            try {
                                lines = FileUtils.readLines(file, "UTF-8");
                            } catch (IOException e) {
                            }
                            logger.debug("loaded The script：{} ,lines : {} , lastModified ：{}, size : {}", file.getName(),
                                    lines != null ? lines.size() : 0, new Date(file.lastModified()), file.length());
                        }
                        count++;
                    } else {
                        logger.info("Not the target type:{}", file.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                logger.error("error script:{}", file.getName());
                logger.error(e.getMessage(), e);
            }
        }
        if (count > 0) {
            logger.info("Loaded {} scripts takes {} milliseconds", count, System.currentTimeMillis() - start);
            //gc
            groovyClassLoaderGC(groovyClassLoader);
        }

    }

    /**
     * 清除groovyClassLoader引用 防止 PermGen Full
     *
     * @param groovyClassLoader
     */
    private void groovyClassLoaderGC(GroovyClassLoader groovyClassLoader) {
        Class[] classes = groovyClassLoader.getLoadedClasses();
        for (Class clazz : classes) {
            ClassInfo.remove(clazz);
//            Introspector.flushFromCaches(clazz);
        }
        groovyClassLoader.clearCache();
        ClassInfo.clearModifiedExpandos();
//        Introspector.flushCaches();
//        System.gc();
    }


    @Override
    public ServiceFactory getServiceFactory() {
        return this.serviceFactory;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    class LastModifiedFileFilter extends AbstractFileFilter implements Serializable {
        private static final long serialVersionUID = -2132740084016138549L;
        private final long cutoff;
        private final boolean acceptOlder;

        public LastModifiedFileFilter(final long cutoff) {
            this(cutoff, true);
        }

        public LastModifiedFileFilter(final long cutoff, final boolean acceptOlder) {
            this.acceptOlder = acceptOlder;
            this.cutoff = cutoff;
        }

        @Override
        public boolean accept(final File file) {
            final boolean newer = FileUtils.isFileNewer(file, cutoff);
            return acceptOlder ? !newer : newer;
        }

        @Override
        public String toString() {
            final String condition = acceptOlder ? "<=" : ">";
            return super.toString() + "(" + condition + cutoff + ")";
        }
    }
}

    