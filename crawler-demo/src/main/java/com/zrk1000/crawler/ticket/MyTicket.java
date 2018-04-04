package com.zrk1000.crawler.ticket;

import com.zrk1000.crawler.principal.SimpleTicket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhouRongKang
 * Date: 2017/8/31
 * Time: 10:59
 */
public class MyTicket extends SimpleTicket {

    private String userId;          //用户唯一标识
    private String username;        //用户名
    private String password;        //密码
    private String captcha;         //验证码

    public MyTicket() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}



    