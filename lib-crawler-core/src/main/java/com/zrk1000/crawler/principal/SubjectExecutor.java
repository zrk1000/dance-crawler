package com.zrk1000.crawler.principal;

import com.zrk1000.crawler.exception.CrawlerException;
import com.zrk1000.crawler.executor.Executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zrk-PC
 * Date: 2017/9/6
 * Time: 16:30
 */
public class SubjectExecutor{
    private Executor executor ;
    private int threadNum = 1;
    private boolean skipPipelings = false;
    private List<String> methods = new ArrayList();

    public SubjectExecutor(Executor executor) {
        this.executor = executor;
    }

    public SubjectExecutor threadNum(int threadNum){
        this.threadNum = threadNum;
        return this;
    }
    public SubjectExecutor skipPipelings(){
        this.skipPipelings = true;
        return this;
    }
    public SubjectExecutor notSkipPipelings(){
        this.skipPipelings = false;
        return this;
    }
    public SubjectExecutor method(String... methods){
        this.methods.addAll(Arrays.asList(methods));
        return this;
    }

    public Object execute(){
        if(methods.size()==0) {
            throw CrawlerException.newInstance(653, "Parameter method must not be empty");
        }
        return executor.execute(this.methods.get(0));
    }

    public void runExecute(){
        if(methods.size()==0) {
            throw CrawlerException.newInstance(653, "Parameter method must not be empty");
        }
        executor.runExecute(this.threadNum,this.skipPipelings,methods.toArray(new String[this.methods.size()]));
    }

    public Map<String, Object> callExecute(){
        if(methods.size()==0) {
            throw CrawlerException.newInstance(653, "Parameter method must not be empty");
        }
        return executor.callExecute(this.threadNum,this.skipPipelings,methods.toArray(new String[this.methods.size()]));
    }

}

    