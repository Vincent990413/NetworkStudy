package com.netty.groupchat.codec;

import com.netty.groupchat.codec.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * 这是Netty的客户端 只需要一个事件循环组 EventLoopGroup 即可
 */
public class NettyClient {
    public static final String LOCAL_ADDRESS = "127.0.0.1";

    public static void main(String[] args) throws InterruptedException {

        //客户端只需要一个事件循环组即可
        EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();

        try {

            //创建客户端的启动器 进行相应的参数配置
            Bootstrap clientBoostrap = new Bootstrap();

            //利用链式编程 设置相关参数
            clientBoostrap.group(clientEventLoopGroup) //绑定事件循环组
                    .channel(NioSocketChannel.class)    //设置客户端通道的 通道实现类型（通过反射处理）
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            //在 Pipeline 中加入 ProtoBufEncoder 编码器
                            pipeline.addLast("encoder", new ProtobufEncoder());
                            pipeline.addLast(new NettyClientHandler());
                        }
                    });

            System.out.println("客户端准备就绪...");


            //让客户端去远程连接服务端
            //这里的 ChannelFuture 涉及到 Netty 本身的异步模型
            ChannelFuture channelFuture = clientBoostrap.connect(LOCAL_ADDRESS, NettyServer.SERVER_PORT).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            clientEventLoopGroup.shutdownGracefully();
        }
    }
}
