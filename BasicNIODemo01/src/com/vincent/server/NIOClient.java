package com.vincent.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO架构下的客户端
 */
public class NIOClient {
    public static void main(String[] args) throws IOException {
        //新建一个Socket通道 设置为非阻塞的
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        //提供服务端的IP地址与端口
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
        if (!socketChannel.connect(address)) {

            while (!socketChannel.finishConnect()) {
                System.out.println("连接需要时间，客户端不会阻塞，可以做其他工作。");
            }
        }

        //连接成功后就发送数据
        String string = "hello, world！";

        //将字节数组包裹进buffer中
        ByteBuffer buffer = ByteBuffer.wrap(string.getBytes());

        //将buffer数据 写入channel中
        socketChannel.write(buffer);

        //代码会停在此处
        System.in.read();
    }
}
