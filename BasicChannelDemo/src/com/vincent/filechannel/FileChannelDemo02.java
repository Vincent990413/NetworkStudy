package com.vincent.filechannel;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 在上一步中 实现了将文本内容 写到文本文件中
 * 现在需要读取 将设已经存在的文本文件
 * 将内容输出到控制台中
 *
 * <p>
 * 那么顺序与刚才肯定是相反的
 * </p>
 */
public class FileChannelDemo02 {
    public static void main(String[] args) {
        FileInputStream fis = null;
        FileChannel fileChannel = null;
        ByteBuffer byteBuffer = null;

        try {
            //利用文件输入流 进行读取
            File file = new File("file_channel/file_channel.txt");
            fis = new FileInputStream(file);

            fileChannel = fis.getChannel();

            //byteBuffer作为数据的重点dst
            byteBuffer = ByteBuffer.allocate((int) file.length());

            fileChannel.read(byteBuffer);

            //将byteBuffer中的字节 转成字符串
            System.out.println("文件内容是:" + new String(byteBuffer.array()));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
