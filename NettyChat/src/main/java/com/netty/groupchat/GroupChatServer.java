package com.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 这是基于Netty的群聊系统中的服务端
 * <p>
 * 可以支持对各个客户端 上线、下线的监听
 * 以及消息的转发
 */
public class GroupChatServer {
    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    /**
     * 处理客户端的请求
     */
    public void handleClientRequest() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //默认是8个

        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取到pipeline 流水线
                            ChannelPipeline pipeline = ch.pipeline();

                            //1. 向pipeline中添加解码器处理器
                            pipeline.addLast("decoder", new StringDecoder());
                            //2. 向pipeline中加入编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            //3. 向pipeline中加入自己的自定义处理器
                            pipeline.addLast(null);
                        }
                    });
            //将通道进行关闭
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            //将事件组进行关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) throws InterruptedException {
        new GroupChatServer(7000).handleClientRequest();
    }
}
