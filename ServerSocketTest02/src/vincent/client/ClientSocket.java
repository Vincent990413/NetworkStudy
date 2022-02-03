package vincent.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 创建客户端01号套接字 ClientSocket
 */
public class ClientSocket {
    public static void main(String[] args) {
        //在本次实例中 将从try-catch-finally改成try-resources
        try (Socket clientSocket = new Socket("127.0.0.1", 8888);
             OutputStream outputStream = clientSocket.getOutputStream();
             DataOutputStream dos = new DataOutputStream(outputStream)) {

            dos.writeUTF("你好，服务器，这里是client01号！");
            dos.flush();

            System.out.println("[客户端] 收到消息，消息内容是：" + new DataInputStream(clientSocket.getInputStream()).readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
