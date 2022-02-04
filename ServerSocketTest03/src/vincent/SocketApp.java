package vincent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 创建服务器套接字 ServerSocket
 * 并且实现，在服务端套接字 接收到客户端套接字信息后 反发消息回去
 * <p>
 * 并且服务端与客户端之间 可以一直收发消息
 * </p>
 *
 *
 * <p>
 * 注意，现在这个版本的程序还是有问题的。为什么？
 * 可以从while的轮询中 看出
 * 对于服务端而言，必须是 读-写 的操作。
 * 在轮询读的过程中，整个程序是处于阻塞状态的！因此，没有读到数据之前，不可以进行写操作
 * <p>
 *
 * <p>
 * 同理而言 客户端也是如此 不能反复读 或者反复写。
 * </p>
 */
public class SocketApp {

    //输入器
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //指定未被占用的端口号 创建一个服务器套接字
        try (ServerSocket serverSocket = new ServerSocket(8888);
             Socket currentSocket = serverSocket.accept()) {

            //不断轮询 实现一直收发消息
            while (true) {
                DataInputStream dis = new DataInputStream(currentSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(currentSocket.getOutputStream());
                //在读数据之前 需要用到 标准的数据输入流来包装
                String message = dis.readUTF();
                System.out.println("[服务端] 收到消息，消息内容是：" + message);
                dataOutputStream.writeUTF(scanner.next());
                dataOutputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
