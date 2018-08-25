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

@Process(domain = "wjj",method = "parse")
public class HousePriceParseProcessor implements PageProcessor {

    private String DOWNLOAD_DIR = "/Users/zrk1000/Desktop/wjj";
    @Override
    public Object process(Visitor visitor, Context context){


        return 1;
    }
}
