package com.vincent.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO架构下的服务端
 */
public class NIOServer {
    public static void main(String[] args) {

        //创建一个ServerSocketChannel 相当于ServerSocket
        ServerSocketChannel serverSocketChannel;
        Selector selector;

        try {
            serverSocketChannel = ServerSocketChannel.open();

            selector = Selector.open();

            //绑定一个端口6666 在服务端监听
            serverSocketChannel.socket().bind(new InetSocketAddress(6666));

            //设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);

            //将这个ServerSocketChannel注册到Selector中去 并且关心的事件是OP_ACCEPT
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //循环等待客户端连接
            while (true) {
                if (selector.select(1000) == 0) { //返回值为0时 表示没有事件发生
                    System.out.println("服务器等待了一秒，无连接！");
                    continue;
                }

                //如果返回的大于0 即有事件 则获取到这些事件的集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    //根据集合中的key对应的事件 去做对应的处理
                    //大致有 accept事件 write事件 read事件等
                    if (key.isAcceptable()) { // 如果有新的客户端连接
                        //给该客户端 生成一个SocketChannel
                        try {
                            //注意，这里要设置SocketChannel为非阻塞的
                            // 因为既然是到了Set集合中 说明是存在这个事件的
                            //说明一定会马上执行accept的 accept也不会阻塞的
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            System.out.println("客户端连接成功！");
                            //将这个socketChannel注册到selector中去
                            //注册selector 关注事件为Read事件 同时给这个socketChannel 关联一个Buffer进行数据的直接交互
                            //注意
                            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (key.isReadable()) { //发生OP_READ事件
                        //通过key 反向获取到对应的channel
                        SocketChannel channel = (SocketChannel) key.channel();

                        //获取到channel关联的buffer
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();

                        try {
                            channel.read(byteBuffer);

                            System.out.println("客户端传过来的数据是：" + new String(byteBuffer.array()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    iterator.remove();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
