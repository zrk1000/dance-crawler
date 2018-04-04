package com.zrk1000.crawler.processor;

import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器注册中心
 * 扫描项目中所有处理器
 * 存储格式：
 *      Map<domain,Map<method,PageProcessor>>
 * User: zrk-PC
 * Date: 2017/9/11
 * Time: 16:23
 */
public abstract class AbstractProcessorRegister implements ProcessorRegister {

    private static Logger logger = LoggerFactory.getLogger(AbstractProcessorRegister.class);

    protected Map<String,Map<String,PageProcessor>> processorRepository = new ConcurrentHashMap();

    @Override
    public Map<String,PageProcessor> regist(String domain, String method, PageProcessor processor){
        Map<String, PageProcessor> pageProcessorMap = processorRepository.get(domain);
        if(pageProcessorMap!=null){
            pageProcessorMap.put(method,processor);
        }else {
            pageProcessorMap = new HashMap();
            pageProcessorMap.put(method,processor);
        }
        return processorRepository.put(domain,pageProcessorMap);
    }
    @Override
    public Map<String,PageProcessor> load(String domain){
        return processorRepository.get(domain);
    }


    public abstract ServiceFactory getServiceFactory();

    /**
     * 扫描@Resource注解标识的字段  在BeanFactory查找服务并注入
     * @param instance
     */
    protected void doInject(Object instance){
        if( getServiceFactory() == null){
            logger.debug("The ServiceFactory is empty");
            return;
        }
        Set<Field> fields = ReflectionUtils.getAllFields(instance.getClass(), ReflectionUtils.withAnnotation(Resource.class));
        for (Field field : fields) {
            Object fieldBean = null;
            Resource resource = field.getAnnotation(Resource.class);
            if (resource.name()!=null && resource.name().length()>0){
                fieldBean = getServiceFactory().getService(resource.name());
            } else {
                fieldBean = getServiceFactory().getService(field.getName());
            }
            if (fieldBean==null ) {
                fieldBean = getServiceFactory().getService(field.getType());
            }
            if (fieldBean!=null) {
                //获取访问权限
                field.setAccessible(true);
                try {
                    field.set(instance, fieldBean);
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(),e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(),e);
                }
            }else {
                logger.info("Not found the bean :" + field.getName() +"");
            }
        }
    }

}

    