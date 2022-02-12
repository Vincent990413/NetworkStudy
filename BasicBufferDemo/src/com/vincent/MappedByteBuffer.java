package com.vincent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer 是 ByteBuffer 的一个子类
 * <p>
 * 1. MappedByteBuffer 可以让文件在内存（堆外内存）直接修改，操作系统不需要拷贝一次
 * </p>
 */
public class MappedByteBuffer {
    public static void main(String[] args) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("mapped_bytebuffer/file.txt", "rw");

            //获取对应通道
            FileChannel channel = randomAccessFile.getChannel();

            //第一个参数是读写模式
            //第二个参数是直接修改的起始位置
            //第三个参数是映射到内存的大小 [0,5)，即将 file.txt 的多少个字节映射到内存
            java.nio.MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

            //能将第一个字符串 改为大写字母O
            map.put(0, (byte) 'O');

            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
