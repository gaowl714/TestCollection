package com.example.netty.longConnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description:
 * @author: GaoWL
 * @create: 2021-07-05 21:12
 **/
public class NettyClient {

    private static EventLoopGroup group;
    private static Bootstrap bootstrap;
    private static ChannelFuture future;

    static {
        //客户端启动辅助类
        bootstrap = new Bootstrap();
        //开启一个线程组
        group = new NioEventLoopGroup();
        //设置socket通道
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.group(group);
        //设置内存分配器
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

}
