package vincent.client;

import vincent.SocketApp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;

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
                //首先收到消息 然后发给所有人
                DataInputStream dis = new DataInputStream(currentSocket.getInputStream());
                String message = dis.readUTF();

                SocketApp.socketList.forEach(socket -> {
                    try {
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.writeUTF(message);
                        dataOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
