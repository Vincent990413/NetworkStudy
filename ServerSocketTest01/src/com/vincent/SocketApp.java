package com.vincent;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 创建服务器套接字 ServerSocket
 */
public class SocketApp {
    public static void main(String[] args) {

        //指定未被占用的端口号 创建一个服务器套接字
        try {
            ServerSocket serverSocket = new ServerSocket(8888);

            System.out.println("------服务器等待连接----------");
            //监听并接受到此套接字的连接 在得到连接之前会一直处于方法阻塞的状态
            Socket currentSocket = serverSocket.accept();

            System.out.println("---------程序连接完毕------------");
            System.out.println("尝试从这次连接中 接受数据...");

            //在读数据之前 需要用到 标准的数据输入流来包装
            InputStream inputStream = currentSocket.getInputStream();
            DataInputStream dis = new DataInputStream(inputStream);
            String message = dis.readUTF();

            System.out.println("信息接收成功，内容为：" + message);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
