package vincent.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * 创建客户端02号套接字 ClientSocket
 */
public class ClientSocket02 {

    //输入器
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //在本次实例中 将从try-catch-finally改成try-resources
        try (Socket currentSocket = new Socket("127.0.0.1", 8888)) {

            // 思考？ 为什么选择要继承Thread类 而不是implements实现Runnable接口
            new InputClient(currentSocket, "客户端2号").start();

            while (true) {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(currentSocket.getOutputStream());
                    dataOutputStream.writeUTF(scanner.next());
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
