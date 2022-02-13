package com.vincent.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 群聊系统中的服务端
 * <p>
 * 首先启动并监听6667端口
 * 服务端接收客户端信息 并实现转发[处理上线与离线]
 * </p>
 */
public class NIOChatRoomServer {

    //定义必要的属性
    private Selector selector;

    //用于监听的Channel
    private ServerSocketChannel listenerChannel;

    private static final int PORT = 6667;

    private static final boolean NON_BLOCKING = false;

    private static final int BUFFER_SIZE = 1024;

    //在构造器中完成初始化工作
    public NIOChatRoomServer() {
        try {
            //获取到选择器
            selector = Selector.open();

            listenerChannel = ServerSocketChannel.open();

            listenerChannel.socket().bind(new InetSocketAddress(PORT));

            //设置非阻塞模式 否则会报错
            listenerChannel.configureBlocking(NON_BLOCKING);

            //将该监听Channel 注册到 选择器上 并且关注监听事件
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 轮询监听方法
     */
    public void listen() {
        try {
            while (true) {
                if (selector.select() == 0) { //返回值为0时 表示没有事件发生
                    System.out.println("服务端等待连接...");
                    continue;
                }

                //得到正处于事件中的Key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    //根据集合中的key对应的事件 去做对应的处理
                    //总共有四个事件

                    //如果是监听事件
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = listenerChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println(socketChannel.getRemoteAddress() + "上线了！");
                    }

                    //发生了读取事件
                    //则要从通道里面读数据 读到buffer里面去
                    if (key.isReadable()) {
                        readMessage(key);
                    }

                    //防止重复处理
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //需要从所有SocketChannel中 排除这个selfChannel
    private void sendMessageToOthers(String message, SocketChannel selfChannel) {

        System.out.println("服务端正在转发消息....");

        //在BIO中 我们使用了一个List来维护 现在直接能得到
        //所有的SocketChannel 通过selector中的keys方法即可
        Set<SelectionKey> keySet = selector.keys();
        Iterator<SelectionKey> iterator = keySet.iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            if (key.channel() != selfChannel && key.channel() instanceof SocketChannel) {
                Channel channel = key.channel();

                SocketChannel targetChannel = (SocketChannel) channel;
                ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
                try {
                    targetChannel.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 服务端读取客户端的消息方法
     *
     * @param key
     */
    private void readMessage(SelectionKey key) {
        //其中读消息 是从这个key中反向找到channel
        SocketChannel channel = null;
        ByteBuffer byteBuffer = null;
        try {
            channel = (SocketChannel) key.channel();
            byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

            int read = channel.read(byteBuffer);
            if (read > 0) {
                String message = new String(byteBuffer.array());
                System.out.println("客户端：" + message);

                //群聊系统中 还需要向其他客户端[Channel 通道]转发消息
                //注意是其他客户端 所以要排除那个 主动发消息的客户端
                //需要传入当前的socketChannel过去 来排除
                sendMessageToOthers(message, channel);
            }
        } catch (IOException e) {
            //注意，如果服务端无法读取。说明客户端离线了
            try {
                System.out.println(channel.getRemoteAddress() + "离线了...");
                //离线之后 不会有任何事件
                //因此需要在SelectionKey集合中 Selector中 取消注册这个key
                //同时需要关闭通道 防止任何操作
                key.cancel();
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }


    public static void main(String[] args) {
        //在服务端的监听方法中 完成一切的轮询工作
        new NIOChatRoomServer().listen();
    }
}
