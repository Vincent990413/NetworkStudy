package com.vincent.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 创建客户端01号套接字 ClientSocket
 */
public class ClientSocket {
    public static void main(String[] args) {
        //连接刚才的服务器 使得服务器能够accept并往下继续执行
        try {
            Socket clientSocket = new Socket("127.0.0.1", 8888);

            //获取该套接字的字节输出流 向服务器写数据 【当发送消息的时候 需要用到流】
            OutputStream outputStream = clientSocket.getOutputStream();


            //2. 在写数据之前 需要用到 标准的数据输出流来包装[与DataInputStream配套使用]
            DataOutputStream dos = new DataOutputStream(outputStream);
            dos.writeUTF("你好，服务器，这里是client01号！");
            dos.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
