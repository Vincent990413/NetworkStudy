package vincent.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 实现文件上传功能的客户端
 * 客户端要做的事情 就是通过当前与服务端连接上的Socket获取
 * 对应的输出流 将图片写过去
 */
public class FileUploadClient {
    public static void main(String[] args) throws IOException {

        Socket socket = null;
        OutputStream outputStream = null;
        FileInputStream fis = null;

        try {
            //建立的套接字
            socket = new Socket("127.0.0.1", 9999);

            outputStream = socket.getOutputStream();

            //准备从项目路径中 读取到输出流中
            fis = new FileInputStream(new File("upload/Java.html"));

            //准备向Socket中写文件
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
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

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
