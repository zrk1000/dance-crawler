package com.zrk1000.crawler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author zhouRongKang
 * @date 2017/11/10 15:49
 */
public class SerializeUtils {

    private static Logger logger = LoggerFactory.getLogger(SerializeUtils.class);

    /**
     * 序列化
     * @param obj
     * @return
     */
    public static String serialize(Object obj) {
        String serStr = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }finally {
            if(objectOutputStream!=null){
                try {objectOutputStream.close();} catch (IOException e1) {}
            }
            if(byteArrayOutputStream!=null) {
                try {byteArrayOutputStream.close(); } catch (IOException e1) { }
            }
        }
        return serStr;
    }

    /**
     * 反序列化
     * @param str
     * @return
     */
    public static <T> T deserialize(String str,Class<T> clazz) {
        T t = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            String redStr = java.net.URLDecoder.decode(str, "UTF-8");
            byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            t= (T)objectInputStream.readObject();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(),e);
        }finally {
            if(objectInputStream!=null){
                try {objectInputStream.close();} catch (IOException e1) {}
            }
            if(byteArrayInputStream!=null){
                try {byteArrayInputStream.close();} catch (IOException e1) {}
            }
        }
        return t;
    }



}
