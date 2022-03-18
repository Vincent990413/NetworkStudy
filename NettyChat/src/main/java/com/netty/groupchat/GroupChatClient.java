package com.netty.groupchat;

import com.netty.groupchat.handler.MyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.SocketAddress;
import java.util.Scanner;

public class GroupChatClient {
    private final String host;

    private final int port;

    private final Scanner scanner = new Scanner(System.in);

    public GroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendRequestToServ() throws InterruptedException {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap client;
        try {
            client = new Bootstrap()
                    .group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            //然后加入一系列的解码器与译码器
                            //1. 向pipeline中添加解码器处理器
                            pipeline.addLast("decoder", new StringDecoder());
                            //2. 向pipeline中加入编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            //3. 向pipeline中加入自己的自定义处理器
                            pipeline.addLast(new MyClientHandler()); //记得这里加入自己的业务处理器
                        }
                    });

            //连接并返回当前通道对象
            final ChannelFuture channelFuture = client.connect(host, port).sync();

            //获得通道对象信息
            Channel channel = channelFuture.channel();
            SocketAddress address = channel.localAddress();

            System.out.println("-------" + address + "--------");

            inputMessageAndSend(channel);
        } finally {
            //关闭客户端
            clientGroup.shutdownGracefully();
        }
    }

    private void inputMessageAndSend(Channel channel) {
        while (scanner.hasNextLine()) {
            //通过channel发送消息到服务端
            channel.writeAndFlush(scanner.nextLine());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new GroupChatClient("127.0.0.1", 7000).sendRequestToServ();
    }
}
