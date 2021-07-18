package com.example.NIO.test.currProgram;

/**
 * @Description:
 * @author: GaoWL
 * @create: 2021-06-06 14:14
 **/
public class SubThread extends Thread {

    private RequestFuture req;

    public SubThread(RequestFuture req) {
        this.req = req;
    }

    public void run() {
        Response res = new Response();
        res.setId(req.getId());
        res.setResult("Server response:" + Thread.currentThread().getId());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RequestFuture.received(res);
    }
}
