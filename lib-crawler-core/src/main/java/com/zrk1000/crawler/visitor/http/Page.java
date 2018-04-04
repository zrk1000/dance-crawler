package com.zrk1000.crawler.visitor.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zrk1000.crawler.util.CharsetDetector;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 页面对象,对httpResponse的扩展处理
 * 用法：
 * 1、Page page = httpResponse.page();
 * 2、Page page = Page.transform(httpResponse);
 *
 * User: zhouRongKang
 * Date: 2017/8/30
 * Time: 10:01
 */
public class Page implements Serializable{

    public static final Logger logger = LoggerFactory.getLogger(Page.class);

    /**
     * 请求url
     */
    private String url;
    /**
     *  响应码
     */
    private Integer code;
    /**
     * 页面字符串 若是html
     */
    private String html;
    /**
     * jsoup的Document对象 若是html
     */
    private Document doc;
    /**
     * 二进制内容
     */
    private byte[] content;
    /**
     *  编码
     */
    private String charset;
    /**
     * 多媒体类型
     */
    private String contentType;
    /**
     * 遍历深度
     */
    private int deep;
    /**
     * 通过contentType解析获得的文件后缀名
     */
    private String extension;

    private List<HttpRequest> httpRequests = new ArrayList();

    /**
     * HttpResponse 转化为 Page
     * @param httpResponse
     * @return
     */
    public static Page transform(HttpResponse httpResponse){
        return new Page(httpResponse);
    }

    public Page() {
    }

    /**
     * 判断是否成功响应
     * @return
     */
    public boolean success(){
        return code > -1;
    }

    public Page(HttpResponse httpResponse) {
        this.url = httpResponse.request()!=null?httpResponse.request().getUrl():null;
        this.code = httpResponse.code();
        this.charset = httpResponse.charset();
        this.contentType = httpResponse.contentType();
        this.content = httpResponse.content();
        this.deep = httpResponse.request()!=null?httpResponse.request().getDeep():-1;
    }

    public JSONObject jsonObject(){
        return JSONObject.parseObject(jsonp2Json(html()));
    }

    public JSONArray jsonArray(){
        return JSONObject.parseArray(jsonp2Json(html()));
    }

    public JSONObject regexJSONObject(String regex){
        return JSONObject.parseObject(regex(regex));
    }

    public JSONObject regexJSONObject(String regex, int group){
        return JSONObject.parseObject(regex(regex, group));
    }

    public JSONArray regexJSONArray(String regex){
        return JSONObject.parseArray(regex(regex));
    }

    public JSONArray regexJSONArray(String regex, int group){
        return JSONObject.parseArray(regex(regex, group));
    }


    /**
     * 获取网页中满足指定css选择器的所有元素的指定属性的集合
     * 例如通过attrs("img[src]","abs:src")可获取网页中所有图片的链接
     *
     * @param cssSelector
     * @param attrName
     * @return
     */
    public ArrayList<String> attrs(String cssSelector, String attrName) {
        ArrayList<String> result = new ArrayList<String>();
        Elements eles = select(cssSelector);
        for (Element ele : eles) {
            if (ele.hasAttr(attrName)) {
                result.add(ele.attr(attrName));
            }
        }
        return result;
    }

    /**
     * 获取网页中满足指定css选择器的所有元素的指定属性的集合
     * 例如通过attr("img[src]","abs:src")可获取网页中第一个图片的链接
     *
     * @param cssSelector
     * @param attrName
     * @return
     */
    public String attr(String cssSelector, String attrName) {
        return select(cssSelector).attr(attrName);
    }


    public ArrayList<String> selectTextList(String cssSelector){
        ArrayList<String> result = new ArrayList<String>();
        Elements eles = select(cssSelector);
        for(Element ele:eles){
            result.add(ele.text());
        }
        return result;
    }

    public String selectText(String cssSelector, int index){
        return getByIndex(selectTextList(cssSelector),index);
    }

    private <T> T getByIndex(List<T> list, int index){
        int realIndex = index;
        if (index < 0) {
            realIndex = list.size() + index;
        }
        return list.get(realIndex);
    }

    public String selectText(String cssSelector) {
        return select(cssSelector).first().text();
    }

    public ArrayList<Integer> selectIntList(String cssSelector){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(String text:selectTextList(cssSelector)){
            result.add(Integer.valueOf(text.trim()));
        }
        return result;
    }

    public int selectInt(String cssSelector, int index){
        String text = selectText(cssSelector,index).trim();
        return Integer.valueOf(text);
    }

    public int selectInt(String cssSelector){
        return selectInt(cssSelector,0);
    }

    public ArrayList<Double> selectDoubleList(String cssSelector){
        ArrayList<Double> result = new ArrayList<Double>();
        for(String text:selectTextList(cssSelector)){
            result.add(Double.valueOf(text.trim()));
        }
        return result;
    }

    public double selectDouble(String cssSelector, int index){
        String text = selectText(cssSelector,index).trim();
        return Double.valueOf(text);
    }

    public double selectDouble(String cssSelector){
        return selectDouble(cssSelector,0);
    }

    public ArrayList<Long> selectLongList(String cssSelector){
        ArrayList<Long> result = new ArrayList<Long>();
        for(String text:selectTextList(cssSelector)){
            result.add(Long.valueOf(text.trim()));
        }
        return result;
    }

    public long selectLong(String cssSelector, int index){
        String text = selectText(cssSelector,index).trim();
        return Long.valueOf(text);
    }

    public long selectLong(String cssSelector){
        return selectLong(cssSelector,0);
    }


    public Elements select(String cssSelector) {
        return this.doc().select(cssSelector);
    }

    public Element select(String cssSelector, int index) {
        Elements eles = select(cssSelector);
        int realIndex = index;
        if (index < 0) {
            realIndex = eles.size() + index;
        }
        return eles.get(realIndex);
    }

    public String regex(String regex, int group, String defaultResult) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html());
        if (matcher.find()) {
            return matcher.group(group);
        } else {
            return defaultResult;
        }
    }

    public String regex(String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html());
        matcher.find();
        return matcher.group(group);
    }

    public String regexAndFormat(String regex, String format){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html());
        matcher.find();
        String[] strs = new String[matcher.groupCount()];
        for(int i=0;i<matcher.groupCount();i++){
            strs[i] = matcher.group(i+1);
        }
        return String.format(format, strs);
    }

    public String regex(String regex, String defaultResult) {
        return regex(regex, 0, defaultResult);
    }

    public String regex(String regex) {
        return regex(regex, 0);
    }



    public void addRequests(List<HttpRequest> requests) {
        httpRequests.addAll(requests);
    }
    public void addRequest(HttpRequest request) {
        httpRequests.add(request);
    }


    /**************getter setter****************/

    /**
     * 返回网页/文件的内容
     *
     * @return 网页/文件的内容
     */
    public byte[] content() {
        return content;
    }

    public void content(byte[] content){
        this.content = content;
    }


    public String url() {
        return url;
    }

    public void url(String url) {
        this.url = url;
    }

    /**
     * 返回网页的源码字符串
     *
     * @return 网页的源码字符串
     */
    public String html() {
        if (html != null) {
            return html;
        }

        if (content == null) {
            return null;
        }
        if (charset == null) {
            charset = CharsetDetector.guessEncoding(content());
        }
        try {
            html = new String(content, charset);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return html;
    }

    /**
     * 设置网页的源码字符串
     *
     * @param html 网页的源码字符串
     */
    public void html(String html) {
        this.html = html;
    }



    /**
     * 返回网页解析后的DOM树
     *
     * @return 网页解析后的DOM树
     */
    public Document doc() {
        if (doc != null) {
            return doc;
        }
        try {
            this.doc = Jsoup.parse(html(), url());
            return doc;
        } catch (Exception ex) {
            logger.info("Exception", ex);
            return null;
        }
    }

    /**
     * 设置网页解析后的DOM树(Jsoup的Document对象)
     *
     * @param doc 网页解析后的DOM树
     */
    public void doc(Document doc){
        this.doc = doc;
    }

    public String charset() {
        if (charset == null) {
            charset = CharsetDetector.guessEncoding(content());
        }
        return charset;
    }

    public void charset(String charset) {
        this.charset = charset;
    }


    public void code(int code){
        this.code = code;
    }

    public int code() {
        return code;
    }

    public int deep() {
        return deep;
    }

    public List<HttpRequest> getHttpRequests() {
        return httpRequests;
    }

    public void setHttpRequests(List<HttpRequest> httpRequests) {
        this.httpRequests = httpRequests;
    }

    /**
     * jsonp 转 json
     * @param jsonp
     * @return
     */
    public String jsonp2Json(String jsonp) {
        if (jsonp == null) {
            return null;
        }
        jsonp = StringUtils.trim(jsonp);
        if (StringUtils.endsWith(jsonp, ";")) {
            jsonp = StringUtils.substringBeforeLast(jsonp, ";");
            jsonp = StringUtils.trim(jsonp);
        }
        if (StringUtils.endsWith(jsonp, ")")) {
            String jsonStr = StringUtils.substringBetween(jsonp, "(", ")");
            jsonStr = StringUtils.trim(jsonStr);
            return jsonStr;
        }
        return jsonp;
    }

    /**
     * 通过contentType解析文件后缀名
     * @return
     */
    public String extension() {
        if(extension == null && contentType != null){
            try {
                String contentTypeTemp = contentType;
                if(contentType.contains(";")){
                    contentTypeTemp = contentType.split(";")[0];
                }
                MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
                MimeType mimeType = allTypes.forName(contentTypeTemp);
                extension = mimeType.getExtension();
            } catch (MimeTypeException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return extension;
    }

}

    