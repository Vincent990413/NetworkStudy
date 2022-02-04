package vincent;

import vincent.client.OutputClient;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 创建服务器套接字 ServerSocket
 * 并且实现，在服务端套接字 接收到客户端套接字信息后 反发消息回去
 * <p>
 * 并且服务端与客户端之间 可以一直收发消息
 * </p>
 */
public class SocketApp {

    public static void main(String[] args) {
        //指定未被占用的端口号 创建一个服务器套接字
        try (ServerSocket serverSocket = new ServerSocket(8888);
             Socket currentSocket = serverSocket.accept()) {

            new OutputClient(currentSocket).start();

            //1. 注意这里不能将写的线程 放在while中 因为会变成不可到达的代码块
            //2. 不能将下方的代码也放入另一个线程中 这样会因为写操作与读操作对同一个socket操作的同步问题
            //我们要做的 只是将读操作与写操作 分离即可！
            while (true) {
                try {
                    DataInputStream dis = new DataInputStream(currentSocket.getInputStream());
                    //在读数据之前 需要用到 标准的数据输入流来包装
                    String message = dis.readUTF();
                    System.out.println("[服务端] 收到消息，消息内容是：" + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
