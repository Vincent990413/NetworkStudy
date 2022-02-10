package vincent.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 实现文件上传功能的服务端
 * 服务端要做的事情 就是接收用户端写过来的图片
 */
public class FileUploadServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        FileOutputStream outputStream = null;
        InputStream is = null;
        Socket currentSocket = null;

        try {
            serverSocket = new ServerSocket(9999);
            currentSocket = serverSocket.accept();

            //获取文件流
            is = currentSocket.getInputStream();

            //文件输出
            outputStream = new FileOutputStream(new File("download/download.html"));

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (currentSocket != null) {
                try {
                    currentSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
