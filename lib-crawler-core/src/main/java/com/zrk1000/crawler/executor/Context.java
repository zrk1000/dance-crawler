package com.zrk1000.crawler.executor;

import com.zrk1000.crawler.principal.Subject;
import com.zrk1000.crawler.principal.Ticket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 上下文 线程安全
 * User: zhouRongKang
 * Date: 2017/8/30
 * Time: 15:48
 */
public class Context extends ConcurrentHashMap<String,Object> {

    private final ThreadLocal<Boolean> threadContext = new ThreadLocal();

    private Subject subject;

    private Ticket ticket;

    private boolean skipPipelings = false;

    public <T> T get(String key,Class<T> clazz){
        Object o = get(key);
        if(o!=null&&clazz!=null){
            if(clazz.isInstance(o)) {
                return clazz.cast(o);
            } else {
                throw new RuntimeException(o + " is not a " + clazz.getName());
            }
        }
        return null;
    }

    public <T> T get(String key,T t){
        Class<T> clazz=(Class<T>) t.getClass();
        Object o = get(key);
        if(o!=null&&clazz!=null){
            if(clazz.isInstance(o)) {
                return clazz.cast(o);
            } else {
                throw new RuntimeException(o + " is not a " + clazz.getName());
            }
        }
        return null;
    }

    public Context(Builder builder) {
        this.ticket = builder.ticket;
        this.subject = builder.subject;
        this.skipPipelings = builder.skipPipelings;
        if(builder.map !=null){
            for(String key:builder.map.keySet()) {
                this.put(key, builder.map.get(key));
            }
        }


    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public boolean isSkipPipelings() {
        Boolean skip = threadContext.get();
        return skip!=null?skip:skipPipelings;
    }

    public void setSkipPipelings(boolean skipPipelings) {
        threadContext.set(skipPipelings);
    }

    public static final class Builder{

        Map<String , Object> map;

        Subject subject;

        Ticket ticket;

        boolean skipPipelings;

        public Builder(){

        }

        public Builder subject(Subject subject){
            if(subject == null){
                throw new IllegalArgumentException("subject is null");
            }
            this.subject = subject;
            return this;
        }

        public Builder token(Ticket ticket){
            if(ticket == null){
                throw new IllegalArgumentException("ticket is null");
            }
            this.ticket = ticket;
            return this;
        }

        public Builder skipPipelings(boolean skipPipelings){
            this.skipPipelings = skipPipelings;
            return this;
        }

        public Builder put(String key,Object value){
            if(key == null){
                throw new IllegalArgumentException("map.key is null");
            }
            if(this.map == null){
                this.map = new ConcurrentHashMap();
            }
            this.map.put(key,value);
            return this;
        }

        public Context build(){
            return new Context(this);
        }




    }

}
    