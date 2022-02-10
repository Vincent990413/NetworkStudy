package vincent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 实现UDP的文件下载
 * 建立连接 并利用输入流
 * 将输入流中的内容 利用输出流
 * 写到本地中
 */
public class UdpFileDownload {
    public static void main(String[] args) {
        InputStream inputStream = null;

        FileOutputStream fos = null;
        try {
            //首先获取到某个文件的下载地址
            URL url = new URL("https://images2018.cnblogs.com/blog/779030/201806/779030-20180616230924616-112752684.png");

            //打开一个URL连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //利用输入流读这个文件 利用输出流写这个文件 写到本地
            inputStream = connection.getInputStream();

            fos = new FileOutputStream("test.png");

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
