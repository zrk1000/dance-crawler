package com.zrk1000.crawler;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: zhouRongKang
 * Date: 2017/8/23
 * Time: 18:42
 */
public class EncodeTest {
    @Test
    public void encode() throws UnsupportedEncodingException {
        String name = "高雁林";
        String code = "%B8%DF%D1%E3%C1%D6";

        System.out.println(new String(name.getBytes(),"UTF-8").toString());
        System.out.println(URLEncoder.encode(name,"gbk"));
    }
}

    