package com.zrk1000.crawler.processor.wjj;

import com.zrk1000.crawler.annotation.Process;
import com.zrk1000.crawler.executor.Context;
import com.zrk1000.crawler.processor.PageProcessor;
import com.zrk1000.crawler.visitor.Visitor;
import com.zrk1000.crawler.visitor.http.HttpRequest;
import com.zrk1000.crawler.visitor.http.Page;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

@Process(domain = "wjj",method = "prices")
public class HousePriceProcessor implements PageProcessor {

    private String HOST = "http://wjj.xa.gov.cn/";
    private String INDEX_URL_PREFIX = HOST + "ptl/def/def/";
    private String INDEX_URL = INDEX_URL_PREFIX + "index_1285_3936_ci_recid_4416419.html";
    private String DOWNLOAD_PAGE_URL = INDEX_URL_PREFIX + "websac/cat/%s.html";
    private String DOWNLOAD_DIR = "/Users/zrk1000/Desktop/wjj";
    @Override
    public Object process(Visitor visitor, Context context){
        System.out.println(111);
        HttpRequest httpRequest = HttpRequest.newBuilder().get(INDEX_URL).build();
        Page page = visitor.visit(httpRequest).page();
        Document doc = page.doc();
        Elements trs = doc.select("#tablelist").select("tr");
        for (Element tr : trs){
            if(!tr.attr("style").equals("height:29px;")){
                continue;
            }
            String indexNum = tr.select("td:eq(0)").text();
            Element td2 = tr.select("td:eq(1)").select("a[id='linkId']").first();
            String href = INDEX_URL_PREFIX + td2.attr("href");
            String name = td2.text();
            String time = tr.select("td:eq(2) > span").text();

            String trid = href.split("trid=")[1];
            String downloadPageUrl = String.format(DOWNLOAD_PAGE_URL,trid);
            Document downloadPage = visitor.visit(HttpRequest.newBuilder().get(downloadPageUrl).build()).page().doc();
            String downloadHref = downloadPage.select("#divContent").select("a").first().attr("href");
            downloadHref = HOST + downloadHref ;
//            System.out.println(indexNum + "#" + name + "#" + href + "#" + time + "#" + downloadHref );
            System.out.println(downloadHref);
            byte[] downloadFileBytes = visitor.visit(HttpRequest.newBuilder().get(downloadHref).build()).page().content();
            File downloadFileDir = new File(DOWNLOAD_DIR);
            if(!downloadFileDir.exists()){
                downloadFileDir.mkdirs();
            }
//            File downloadFile = new File(DOWNLOAD_DIR + "/" + name + "-" + time + ".zip");
            File downloadFile = new File(DOWNLOAD_DIR + "/" + name + "-" + time + ".zip");

            try {
                FileUtils.writeByteArrayToFile(downloadFile,downloadFileBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return 1;
    }
}
