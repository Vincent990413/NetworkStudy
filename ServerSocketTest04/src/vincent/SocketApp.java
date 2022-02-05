package vincent;

import vincent.client.OutputClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建服务器套接字 ServerSocket
 * 并且实现，在服务端套接字 接收到客户端套接字信息后 反发消息回去
 *
 * <p>
 * 并且可以实现群聊 即一个服务端 多个客户端
 * 怎么实现多个客户端都能被一个服务端监听到呢？
 * 将监听accept放在 while循环中
 * </p>
 *
 * <p>
 * 而且如何做到让某一个客户端发的信息 变成群聊呢？
 * 那就是： 比如客户端 c1 c2 c3都连上了服务端
 * 倘若c1向服务端发送消息 则服务端在接收到消息后
 * 又发给其他已经监听到的客户端
 * </p>
 *
 * <p>
 * 注意这个操作是要放在while中的 每次有人上线 则开一个线程
 * 专门负责消息的转发：读取 + 转发
 * </p>
 */
public class SocketApp {

    //用于维护每次的会话连接套接字
    public final static List<Socket> socketList = new ArrayList<>();

    public static void main(String[] args) {
        //指定未被占用的端口号 创建一个服务器套接字
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("服务端开启成功，正在等待连接...");

            while (true) {
                try {
                    //轮询地监听多个客户端
                    Socket currentSocket = serverSocket.accept();

                    //监听到某一个后 添加到socket集合中
                    socketList.add(currentSocket);

                    System.out.println("连接数+1，当前在线人数为：" + socketList.size());

                    //收到信息后 服务端广播给所有人
                    new OutputClient(currentSocket).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
