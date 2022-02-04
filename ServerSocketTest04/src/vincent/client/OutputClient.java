package vincent.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * 这个类的作用 就是专门负责负责写消息到Socket中
 */
public class OutputClient extends Thread {

    private Socket currentSocket = null;

    //输入器
    private static Scanner scanner = new Scanner(System.in);


    public OutputClient() {
    }

    public OutputClient(Socket currentSocket) {
        this.currentSocket = currentSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(currentSocket.getOutputStream());
                dataOutputStream.writeUTF(scanner.next());
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
