package com.netty.groupchat;

import com.netty.groupchat.handler.MyServerHandler;
import com.netty.groupchat.handler.MyServerHeartBeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 这是基于Netty的群聊系统中的服务端
 * <p>
 * 并且在该服务端中 增加心跳检测机制
 */
public class GroupChatServerHeartBeat {
    private int port;

    private GroupChatServerHeartBeat(int port) {
        this.port = port;
    }


    //处理客户端的请求
    private void handleClientRequest() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //默认是8个

        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) //设置服务端的处理器为LoggingHandler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取到pipeline 流水线
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
                            pipeline.addLast(new MyServerHeartBeatHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            //将事件组进行关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new GroupChatServerHeartBeat(7000).handleClientRequest();
    }
}
