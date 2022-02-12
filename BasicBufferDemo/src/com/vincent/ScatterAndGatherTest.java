package com.vincent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * 所谓 Scattering 就是将数据写入 Buffer 时，可以采用 Buffer 数组，依次写入
 * 所谓 Gathering 就是从 buffer 中读取数据时 可以采用 buffer 数组 依次读出
 */
public class ScatterAndGatherTest {
    public static void main(String[] args) throws IOException {
        //使用 ServerSocketChannel 和 SocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(8888);

        //绑定端口到socket 并启动
        serverSocketChannel.socket().bind(address);

        //在服务器端创建 Buffer 数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接 使用telnet
        SocketChannel currentSocket = serverSocketChannel.accept();

        //假设最多消息长度为8 这样更能看出效果
        int messageLength = 8;

        //轮询读取 每发一次就接受一次
        while (true) {
            int byteRead = 0;

            while (byteRead < messageLength) {

                //可以看到，这里使用数组作为读取的参数 会自己选择用哪个字节Buffer来读
                long read = currentSocket.read(byteBuffers);
                byteRead += read;

                System.out.println("byteRead=" + byteRead);

                //使用流打印 看看当前的这个buffer的position和limit
                Arrays.asList(byteBuffers)
                        .stream()
                        .map(buffer -> "position=" + buffer.position() + "，limit=" + buffer.limit())
                        .forEach(System.out::println);
            }

            //将所有的buffer进行状态反转flip 因为将数据读到buffer中
            Arrays.asList(byteBuffers).forEach(Buffer::flip);

            //将数据读出显示到控制台
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                long write = currentSocket.write(byteBuffers);
                byteWrite += write;
            }

            //写完之后，将Buffer复位
            Arrays.asList(byteBuffers).forEach(Buffer::clear);

            //最后打印相应的信息
            System.out.println("byteWrite=" + byteWrite + "，messageLength" + messageLength);
        }

    }
}
