package com.netty.groupchat.codec.handler;

import com.netty.groupchat.codec.pojo.MyProtoInfo;
import com.netty.groupchat.codec.pojo.StudentPOJO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;
import java.util.EnumSet;

/**
 * 我们自定义一个Handler 需要继承 Netty 规定好的某个 HandlerAdapter
 * 此时 我们相当于 规定 对于服务端来说 当消息是入站 InBound 的时候 执行什么操作
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * @param ctx 上下文对象，含有管道 Pipeline 信息，也能拿到通道 Channel 信息 以及地址等信息
     * @param msg 对于服务端来说：当消息入站时 其拿到的客户端的数据 默认类型为 Object 类型
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //读取从客户端发送过来的StudentPOJO.student
//        StudentPOJO.Student student = (StudentPOJO.Student) msg;
//        System.out.println("客户端发送的数据中， id = " + student.getId());
//        System.out.println("客户端发送的数据中， name = " + student.getName());
//

        //如果是不同的类型 根据类型去判断
        MyProtoInfo.MyMessage myMessage = (MyProtoInfo.MyMessage) msg;
        MyProtoInfo.MyMessage.DataType dataType = myMessage.getDataType();
        if (dataType == MyProtoInfo.MyMessage.DataType.StudentType) {
            System.out.println("是学生对象！");
        } else {
            System.out.println("是工人对象！");
        }

    }

    /**
     * 当服务端读取完从客户端传来的数据后 (即数据读取成功后的操作)
     *
     * @param ctx 上下文对象 此时应该与缓冲区交互
     * @throws Exception 可能出现的异常
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //WriteAndFlush = write + flush 即写入再刷新
        //一般而言 我们对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello，客户端!", CharsetUtil.UTF_8));
    }

    /**
     * 发生异常后应该执行什么操作 需要关闭通道 防止下一步的错误
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
