package com.vincent.filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 在demo01与demo02中
 * 我们分别完成了文件的读取与写入 并且用到了两个ByteBuffer
 *
 * <p>
 * 在本次案例中 我们将只使用一个 ByteBuffer
 * 一个输入流 一个输出流 以及它们各自的 FileChannel 来与 Buffer 交互
 * 首先从demo03.txt中读取内容
 *
 * <p>
 * 然后输出到另一个文件中去
 * </p>
 */
public class FileChannelDemo03 {
    public static void main(String[] args) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fileInputChannel = null, fileOutputChannel = null;
        ByteBuffer byteBuffer;

        try {
            fis = new FileInputStream("demo03/demo03.txt");
            fileInputChannel = fis.getChannel();

            fos = new FileOutputStream("demo03/demo03_output.txt");
            fileOutputChannel = fos.getChannel();

            byteBuffer = ByteBuffer.allocate(1024);

            //利用buffer 轮询读取源文件demo03.txt
            while (true) {
                //清理缓存 准备写到缓存区中
                byteBuffer.clear();

                int read = fileInputChannel.read(byteBuffer);
                if (read == -1) {
                    break;
                }

                //转换成从buffer中读取数据的状态
                byteBuffer.flip();

                //开始写
                fileOutputChannel.write(byteBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
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
            if (fileInputChannel != null) {
                try {
                    fileInputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileOutputChannel != null) {
                try {
                    fileOutputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
