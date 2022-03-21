package com.netty.groupchat.codec.inializer;

import com.netty.groupchat.codec.handler.NettyServerHandler;
import com.netty.groupchat.codec.pojo.MyProtoInfo;
import com.netty.groupchat.codec.pojo.StudentPOJO;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * 自定义的通道初始器
 */
public class MyChannelInializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //给 Pipeline 设置处理器
        ChannelPipeline pipeline = ch.pipeline();

        //因为是从客户端那边传来的数据 所以对于服务端来说
        //是要从二进制的数据中 译码成业务数据 指定对那种对象进行译码
//        pipeline.addLast("decoder", new ProtobufDecoder(StudentPOJO.Student.getDefaultInstance()));

        //如果是message引用其他message 就传入外部的类
        pipeline.addLast("decoder", new ProtobufDecoder(MyProtoInfo.MyMessage.getDefaultInstance()));

        pipeline.addLast(new NettyServerHandler()); //增加处理器
    }
}
