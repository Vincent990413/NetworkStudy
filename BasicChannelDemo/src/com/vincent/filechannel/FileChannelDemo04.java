package com.vincent.filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 在本次demo中 使用FileChannel和
 * transferFrom方法完成文件的拷贝
 *
 * <p>
 * 拷贝一份图片 通道与通道之间使用transferFrom
 * </p>
 */
public class FileChannelDemo04 {
    public static void main(String[] args) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fileInputChannel = null, fileOutputChannel = null;

        try {
            //创建相应的流与对应的通道
            fis = new FileInputStream("image/code.png");
            fileInputChannel = fis.getChannel();

            fos = new FileOutputStream("image/code_output.png");
            fileOutputChannel = fos.getChannel();

            //使用transferFrom完成拷贝 从input通道拷贝到output通道中
            fileOutputChannel.transferFrom(fileInputChannel, 0, fileInputChannel.size());
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
