package vincent.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 这个类的作用 就是专门负责读当前Socket的会话
 */
public class InputClient extends Thread {

    private Socket currentSocket = null;

    private String clientName;

    public InputClient() {
    }

    public InputClient(Socket currentSocket, String clientName) {
        this.currentSocket = currentSocket;
        this.clientName = clientName;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DataInputStream dis = new DataInputStream(currentSocket.getInputStream());
                //在读数据之前 需要用到 标准的数据输入流来包装
                String message = dis.readUTF();
                System.out.println("[" + clientName + "] 收到消息，消息内容是：" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
