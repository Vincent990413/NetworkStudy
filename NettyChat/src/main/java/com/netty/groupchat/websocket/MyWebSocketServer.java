package com.netty.groupchat.websocket;

import com.netty.groupchat.websocket.handler.MyTextWebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class MyWebSocketServer {

    private int port;

    public MyWebSocketServer(int port) {
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

                            //因为是基于Http协议，因此要使用对应的Http解译码器
                            pipeline.addLast(new HttpServerCodec());

                            //可以支持写大量的数据流 而不会影响内存
                            pipeline.addLast(new ChunkedWriteHandler());

                            //由于Http数据在传输过程中 是分段传输的
                            //而这个HttpObjectAggregator 可以将多个段 聚合在一起
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            //对应WebSocket的数据是以Frame帧的形式传输的
                            //浏览器请求时，ws://localhost:7000/xxx 表示请求的uri(注意如果这里是xxx 那么在浏览器那里 也要保持一致)
                            //WebSocketServerProtocolHandler 核心功能是将http协议升级为ws协议 以保持长连接
                            pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));

                            //增加自己的业务处理
                            pipeline.addLast(new MyTextWebSocketFrameHandler());
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
        new MyWebSocketServer(7000).handleClientRequest();
    }
}