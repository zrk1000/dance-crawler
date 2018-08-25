package com.zrk1000.crawler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取根域名工具类
 *
 * @author zhouRongKang
 * @date 2018/2/2 13:59
 */
public class RootDomainUtils {

    private static Logger logger = LoggerFactory.getLogger(RootDomainUtils.class);


    //定义好获取的域名后缀。如果还有要定义的请添加域名的后缀) 。
    public static final String REG_STR = "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co)\\b()*";

    public static final Pattern pattern = Pattern.compile(REG_STR);

    /**
     * 获取根域名
     *
     * @param urlStr
     * @return
     */
    public static String getRootDomain(String urlStr) {
        String domain = null;
        try {
            Matcher matcher = pattern.matcher(urlStr);
            while (matcher.find()) {
                domain = matcher.group();
            }
        } catch (Exception e) {
            logger.warn("Failed to get the root domain name. urlStr : {}", urlStr);
        }
        return domain;
    }

    /**
     * 获取二级域名
     *
     * @param urlStr
     * @return
     */
    public static String getSecondaryDomain(String urlStr) {
        String rootDomain = RootDomainUtils.getRootDomain(urlStr);
        if(rootDomain != null){
            return "." + rootDomain;
        }
        return null;
    }

//    public static void main(String[] args) {
//        String rootDomain = RootDomainUtils.getSecondaryDomain("www.gs.baidu.com");
//        System.out.println(rootDomain);
//    }
}
