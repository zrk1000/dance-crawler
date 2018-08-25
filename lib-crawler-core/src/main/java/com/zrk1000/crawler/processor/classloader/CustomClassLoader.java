package com.zrk1000.crawler.processor.classloader;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义classLoader
 */
public class CustomClassLoader extends URLClassLoader {

    private static Logger logger = LoggerFactory.getLogger(CustomClassLoader.class);

    private Map<String, byte[]> cacheClassBytes;

    private JavaCompiler compiler;

    private ClassFileManager fileManager;


    public CustomClassLoader(URLClassLoader classLoader) {
        super(new URL[0], classLoader);
        this.cacheClassBytes = new HashMap();
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = cacheClassBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        return defineClass(name, buf, 0, buf.length);
    }


    /**
     * 将源码解析为Class
     *
     * @param fileName  java文件名称  eg. aaa.java
     * @param javaCode  源码字符串
     * @return 编译通过返回相应对象Class，不通过则为null
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<?> parseClass(String fileName, String javaCode) throws ClassNotFoundException {
        long start = System.currentTimeMillis();

        List<StringFileObject> compileUnits = new ArrayList(1);
        compileUnits.add(new StringFileObject(fileName, javaCode));
        List<String> options = new ArrayList(4);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compileUnits);
        boolean success = task.call().booleanValue();
        if (success) {
            ByteFileObject fileObject = fileManager.getCachedObject();
            cacheClassBytes.put(fileObject.getClassName(),fileObject.getBytes());
            Class clazz = this.loadClass(fileObject.getClassName());
            long end = System.currentTimeMillis();
            logger.info("compile success，fileName ： {}，用时: {}ms",fileName,end - start);
            return clazz;
        }

        return null;
    }

    /**
     * 将源码文件解析为Class
     * @param file
     * @return
     */
    public Class<?> parseClass(File file) {
        try {
            String name = file.getName();
            String source = FileUtils.readFileToString(file,"UTF-8");
            return parseClass(name,source);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear(){
        this.cacheClassBytes.clear();
        this.cacheClassBytes = null;
        this.compiler = null;
        this.fileManager = null;
    }


}
