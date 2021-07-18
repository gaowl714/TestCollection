package com.example.NIO.test.currProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: GaoWL
 * @create: 2021-06-06 13:52
 **/
public class FutureMain {
    public static void main(String[] args) {
        List<RequestFuture> reqs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            RequestFuture req = new RequestFuture();
            req.setId((long) i);
            req.setRequest("Hello world");
            RequestFuture.addFuture(req);
            reqs.add(req);
            send(req);
            SubThread subThread = new SubThread(req);
            subThread.start();
        }

        for (RequestFuture req : reqs) {
            Object result = req.get();
            System.out.println(result);
        }
    }

    private static void send(RequestFuture req) {
        System.out.println("客户端发送数据：" + req.getRequest());
    }
}
