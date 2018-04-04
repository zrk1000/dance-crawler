package com.zrk1000.crawler;

import com.zrk1000.crawler.visitor.okhttp.OKHttpPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/9/4
 * Time: 17:03
 */
public class OkHttpTest {

    OkHttpClient okHttpClient;

    protected String yzmUrl = "https://persons.shgjj.com/VerifyImageServlet";
    protected String detailUrl = "https://persons.shgjj.com/MainServlet?ID=11";

    @Before
    public void before(){
        OKHttpPool okHttpPool = new OKHttpPool();
        okHttpClient = okHttpPool.getClientBuilder().build();
    }

    @Test
    public void okHttpTest() throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.url(detailUrl).
                header("Cookie","_gscu_1668400483=9066467444bfu126; Hm_lvt_fa5185c6a620e90153a6a54d650500d3=1503538418,1503559594,1504604713,1505371525; __utma=176600100.229657103.1498542025.1505376590.1505379042.11; __utmz=176600100.1505379042.11.9.utmcsr=shgjj.com|utmccn=(referral)|utmcmd=referral|utmcct=/; Hm_lpvt_fa5185c6a620e90153a6a54d650500d3=1505377167; _gscbrs_1668400483=1; JSESSIONID=98E6893041F59634F1E836E6420159F6; __utmc=176600100; __utmb=176600100.8.10.1505379042; __utmt=1").build();
        Response response = okHttpClient.newCall(request).execute();
//        FileCopyUtils.copy(response.body().bytes(),new File("D:/aaa.png"));
        FileCopyUtils.copy(response.body().bytes(),new File("D:/aaa.html"));

    }
}

    