package com.vincent.filechannel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 将某个字符串写入到某个文件中
 * 那么整个流程是什么呢？⭐ 很重要：
 *
 * <p>
 * 1. 首先将字符串写入到缓冲区中（数据直接与缓冲区交互）
 * 2. 然后数据到 FileChannel中 对缓冲区来说是读状态
 * 3. 然后用channel将数据写到文件 file_channel.txt中
 * </p>
 */
public class FileChannelDemo01 {
    public static void main(String[] args) {
        String content = "Hello, this is the content!";

        //对文件操作需要文件输出流
        ByteBuffer byteBuffer = null;
        FileChannel fileChannel = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("file_channel/file_channel.txt");

            //通过文件输出流获得对应的文件通道 FileChannel
            //而这个fileChannel的真实类型是FileChannelImpl类
            fileChannel = fos.getChannel();

            //数据交互是需要缓冲区的 这里使用ByteBuffer最好
            byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put(content.getBytes());

            //转换成读状态 因为fileChannel要用src为byteBuffer的对象
            byteBuffer.flip();

            //这里不让流直接与文件进行接触
            fileChannel.write(byteBuffer);

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
