package com.vincent;

import java.nio.IntBuffer;
import java.nio.channels.Channel;

/**
 * 这个案例简要介绍了三大组件之一：
 * Buffer 组件
 */
public class BasicBufferDemo {
    public static void main(String[] args) {
        //利用相应的静态工厂类 创建一个固定长度的Buffer 分配整型缓冲区
        final int BUFFER_SIZE = 5;
        IntBuffer intBuffer = IntBuffer.allocate(BUFFER_SIZE);

        //利用Buffer的put方法 往缓冲区中添加数据
        for (int i = 0; i < 5; i++) {
            intBuffer.put(i);
        }

        //使用put方法添加数据后 使用flip方法进行读写切换
        //切换后 position会置为0
        intBuffer.flip();

        //hasRemaining 方法判断是否当前位置小于limit
        while (intBuffer.hasRemaining()){
            //get方法 首先读取当前位置的数据 然后position增加
            System.out.println(intBuffer.get());
        }

    }
}
