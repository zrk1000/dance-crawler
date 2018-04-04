package com.zrk1000.crawler;

import com.zrk1000.crawler.session.Session;
import com.zrk1000.crawler.session.SessionManager;
import com.zrk1000.crawler.session.SimpleSessionManager;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: zhouRongKang
 * Date: 2017/8/21
 * Time: 14:30
 */
public class SessionManagerTest {

    @Test
    public void sessionTest(){
        SessionManager sessionManager = new SimpleSessionManager();
        Session session = sessionManager.getSession("1231");
        session.setAttribute("aaa","bbb");

        Session session1 = sessionManager.getSession("1231");
        Object aaa = session1.getAttribute("aaa");
        System.out.println(aaa);
    }
}

    