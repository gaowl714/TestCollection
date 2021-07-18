package com.example.NIO.test.currProgram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @author: GaoWL
 * @create: 2021-06-06 13:54
 **/
public class RequestFuture {
    private static Map<Long, RequestFuture> futures = new ConcurrentHashMap<>();
    private Long id;
    private Object request;
    private Object result;

    public static void addFuture(RequestFuture req) {
        futures.put(req.getId(), req);
    }

    public static void received(Response res){
        RequestFuture future = futures.remove(res.getId());
        if (future != null) {
            future.setResult(res.getResult());
            synchronized (future) {
                future.notify();
            }
        }
    }

    public Object get(){
        synchronized(this){
            while (this.result == null) {
                try {
                    this.wait(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
