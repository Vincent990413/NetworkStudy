package com.netty.groupchat.codec.handler;

import com.netty.groupchat.codec.pojo.MyProtoInfo;
import com.netty.groupchat.codec.pojo.StudentPOJO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Random;

/**
 * 类似
 *
 * @see NettyServerHandler
 * 这里主要规定了 当客户端收到来自服务端的消息时、当发生异常时、当通道激活时 应该执行什么操作？
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道激活之后 就会触发该方法
     *
     * @param ctx 上下文对象
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        /**
         * 发送一个 Student 对象到服务端 且这个对象
         * 是由 protoc.exe 编译后的类的内部类
         */
        StudentPOJO.Student student = StudentPOJO.Student
                .newBuilder()
                .setId(4)
                .setName("张三")
                .build();
//        ctx.writeAndFlush(student);


        /**
         * 随机发送一个 Worker或者Student 对象到服务端 且这个对象是...
         */
        int randomInt = new Random().nextInt(3);
        MyProtoInfo.MyMessage myMessage = null;

        if (randomInt == 0) {
            myMessage = MyProtoInfo.MyMessage
                    .newBuilder()
                    .setDataType(
                            MyProtoInfo.MyMessage.DataType.StudentType
                    )
                    .setStudent(
                            //创建 Student 实例
                            MyProtoInfo.Student.newBuilder().setName("学生1").setId(1).build()
                    ).build();//创建外部对象实例
        }

        if (randomInt == 1) {
            myMessage = MyProtoInfo.MyMessage
                    .newBuilder()
                    .setDataType(
                            MyProtoInfo.MyMessage.DataType.WorkerType
                    )
                    .setWorker(
                            //创建 Worker 实例
                            MyProtoInfo.Worker.newBuilder().setName("工人1").setAge(20).build()
                    ).build();//创建外部对象实例

        }

        ctx.writeAndFlush(myMessage);
    }

    /**
     * 当从服务端中的消息 入站时 我们应该做的操作
     *
     * @param ctx 上下文对象
     * @param msg 从服务端那边传过来的消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务端发过来的消息为" + byteBuf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 当异常发生时 应该做的操作
     *
     * @param ctx   上下文对象
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
