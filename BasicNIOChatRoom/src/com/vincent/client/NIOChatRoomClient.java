package com.vincent.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 连接服务端
 * 发送消息给服务端
 * 接收服务端发过来的消息
 */
public class NIOChatRoomClient {

    private final static String HOST = "127.0.0.1";

    private final static int PORT = 6667;

    private Selector selector;

    private SocketChannel socketChannel;

    private String userName;

    //构造器 完成初始化工作
    public NIOChatRoomClient() throws IOException {
        selector = Selector.open();

        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);

        userName = socketChannel.getLocalAddress().toString().substring(1);

        System.out.println(userName + "客户端准备就绪...");
    }

    //向服务端发送消息
    public void sendMessage(String message) {
        message = userName + "说：" + message;
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取从服务端回复的消息
    public void receiveMessage() {
        int readChannel = 0;
        try {
            readChannel = selector.select(2000);
            //若大于0 则有事件发生的通道
            if (readChannel > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        channel.read(byteBuffer);
                        byte[] array = byteBuffer.array();
                        System.out.println("服务端说：" + new String(array));
                    }
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //启动客户端
        NIOChatRoomClient nioChatRoomClient = new NIOChatRoomClient();

        //开启一个线程 每隔三秒钟去读取服务端的数据
        new Thread(() -> {
            while (true) {
                nioChatRoomClient.receiveMessage();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //不间断地写消息给服务端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            nioChatRoomClient.sendMessage(message);
        }
    }

}
