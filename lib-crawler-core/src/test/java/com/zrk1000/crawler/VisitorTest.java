package com.zrk1000.crawler;

import com.zrk1000.crawler.visitor.OkHttpVisitor;
import com.zrk1000.crawler.visitor.Visitor;
import com.zrk1000.crawler.visitor.http.HttpRequest;
import com.zrk1000.crawler.visitor.http.HttpRequestBody;
import com.zrk1000.crawler.visitor.http.HttpResponse;
import com.zrk1000.crawler.visitor.http.Page;
import com.zrk1000.crawler.visitor.okhttp.OKHttpPool;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: zhouRongKang
 * Date: 2017/8/29
 * Time: 16:14
 */
public class VisitorTest {

    private Logger logger = LoggerFactory.getLogger(OkHttpVisitor.class);

    private Visitor visitor;
    private OkHttpClient okHttpClient;

    @Before
    public void before(){
        OKHttpPool okHttpPool = new OKHttpPool();
        visitor = new OkHttpVisitor(okHttpPool);
        okHttpClient = okHttpPool.getClientBuilder().build();
    }

    @Test
    public void testBaiDu(){
        HttpRequest httpRequest = HttpRequest.newBuilder().get("http://www.baidu.com").build();
        HttpResponse httpResponse = visitor.visit(httpRequest);
        System.out.println(httpResponse.page().doc());
    }


    @Test
    public void testAppapiGet() {
        //http://devstockapi.gs.youyuwo.com/recommend?key=lobby_stock_list
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .get("http://devstockapi.gs.youyuwo.com/recommend")
                .param("key","lobby_stock_list")
                .requestBody(HttpRequestBody.json(""))
                .build();
        HttpResponse httpResponse = visitor.visit(httpRequest);
        System.out.println(httpResponse.page().jsonObject());
    }
    @Test
    public void testOkHttpPostPart()throws IOException {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), File.createTempFile("pre","suf"));
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("aaa","bbb")
                .addFormDataPart("ddd","eee")
                .addFormDataPart("image", "test.jpg", fileBody)
                .addPart(Headers.of("Content-Disposition", "form-data; name=ffff"),
                        RequestBody.create(null,"gggg"))
                .build()
                ;
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .post(formBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        System.out.println(response.code());
    }

    @Test
    public void testAppapiPost(){
        //    http://devgjjapi.gs.youyuwo.com/appapi/login/test?username=18500279884&password=qqqqqq1
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .post("http://devgjjapi.gs.youyuwo.com/appapi/login/test")
                .param("username","18500279884")
                .param("password","qqqqqq1")
                .build();
        HttpResponse httpResponse = visitor.visit(httpRequest);
        System.out.println(httpResponse.page().jsonObject());
    }

    @Test
    public void testAppapiPostOfOkHttp() throws IOException {
        //    http://devgjjapi.gs.youyuwo.com/appapi/login/test?username=18500279884&password=qqqqqq1
        String url = "http://devgjjapi.gs.youyuwo.com/appapi/login/test";
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username","18500279884");
        builder.add("password","qqqqqq1");
        RequestBody requestBody = builder.build();
        //create builder
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);
        Response response = okHttpClient.newCall(requestBuilder.build()).execute();
        HttpResponse httpResponse = transformResponse(response);
        System.out.println(httpResponse.page().jsonObject());
    }

    @Test
    public void testJsonp2json(){
        Page page = new Page();
        System.out.println(page.jsonp2Json("localHandler({\"result\":\"我是远程js带来的数据\"});"));
    }


    private HttpResponse transformResponse(Response response){
        ResponseBody responseBody = response.body();
        int code = response.code();
        byte[] content = null;
        String contentType = null;
        String charset = null;
        if(responseBody != null){
            MediaType mediaType = responseBody.contentType();
            if(mediaType!=null){
                contentType = mediaType.toString();
                charset = mediaType.charset()!=null?mediaType.charset().name():null;
            }
            try {
                content = responseBody.bytes();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage(),e);
            }
        }
        HttpResponse httpResponse = new HttpResponse(200,contentType,content);
        httpResponse.charset(charset);
        httpResponse.code(code);
        return httpResponse;
    }
}

    