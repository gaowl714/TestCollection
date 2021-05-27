package com.example.NIO;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ConnectHandler extends ChannelInboundHandlerAdapter {

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        //当一个新的连接已经被建立时，channelActive(ConnectHandler)将会被调用
//        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
//
//
//        Channel channel = null;
//        //异步地连接到远程节点
//        ChannelFuture channelFuture = channel.connect(new InetSocketAddress("192.168.0.1", 25));
//        //注册一个 ChannelFutureListener，以便在操作完成时获得通知
//        channelFuture.addListener((ChannelFutureListener) future -> {
//            //如果操作是成功的，则创建一个 ByteBuf 以持有数据
//            if (future.isSuccess()) {
//                ByteBuf buffer = Unpooled.copiedBuffer(
//                        "Hello", Charset.defaultCharset());
//                //将数据异步地发送到远程节点，返回一个 ChannelFuture
//                ChannelFuture wf = future.channel()
//                        .writeAndFlush(buffer);
//            }
//            //如果发生错误，则访问描述原因的 Throwable
//            else {
//                Throwable cause = future.cause();
//                cause.printStackTrace();
//            }
//        });
//    }
}