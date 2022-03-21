package com.netty.groupchat.codec;

import com.netty.groupchat.codec.inializer.MyChannelInializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 这是 Netty 的服务端
 */
public class NettyServer {
    public static final int SERVER_PORT = 6668;

    public static void main(String[] args) throws InterruptedException {
        //1. 创建 BossGroup 以及 WorkerGroup
        //2. bossGroup 与 workerGroup工作时 底层都会无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup(); //bossGroup 只支持连接请求
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //而真正与客户端业务处理 交给 workerGroup 完成

        try {
            //配置启动服务端的参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            //使用链式编程的方式来进行设置相应的参数
            serverBootstrap
                    .group(bossGroup, workerGroup) //绑定 boss 与 worker 两个线程组
                    .channel(NioServerSocketChannel.class) //设置 NioSocketChannel 作为其 Channel 类型
                    .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列得到连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                    .childHandler(new MyChannelInializer()); //给 WorkerGroup 的 EventLoop 的对应管道设置处理器

            System.out.println("服务端已经准备好...");

            //绑定端口是异步操作 并给这个绑定行为 增加监听器
            ChannelFuture channelFuture = serverBootstrap.bind(SERVER_PORT).sync();
            channelFuture.addListener((ChannelFuture cf) -> {
                if (cf.isSuccess()) {
                    System.out.println("监听端口成功！");
                } else {
                    System.out.println("监听端口失败！");
                }
            });

            //对通道的关闭进行监听
            channelFuture.channel().closeFuture().sync();

        } finally {
            //关闭 BossGroup
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
