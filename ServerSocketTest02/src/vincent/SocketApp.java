package vincent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 创建服务器套接字 ServerSocket
 * 并且实现，在服务端套接字 接收到客户端套接字信息后 反发消息回去
 * <p>
 * 但是注意 在这个版本中 只能实现一次通信，为什么？
 * 因为，在服务端，accept监听到客户端套接字的内容后。
 * 并且完成：1. 收消息 2. 重发消息 两个操作后 资源已经关闭！
 * <p>
 * 下一步将实现，无限次的收发消息，不会收发完之后就关闭。
 */
public class SocketApp {
    public static void main(String[] args) {
        //指定未被占用的端口号 创建一个服务器套接字
        try (ServerSocket serverSocket = new ServerSocket(8888);
             Socket currentSocket = serverSocket.accept();//监听客户端
             DataInputStream dis = new DataInputStream(currentSocket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(currentSocket.getOutputStream())) {

            //在读数据之前 需要用到 标准的数据输入流来包装
            String message = dis.readUTF();
            System.out.println("[服务端] 收到消息，消息内容是：" + message);
            dataOutputStream.writeUTF("消息已经收到，这里是服务端！");
            dataOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
