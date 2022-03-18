package com.netty.groupchat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 这个是客户端的入站处理器 当数据从服务端发送进入客户端的 Pipeline 时
 * <p>
 * 并且规定了数据的类型是String类型 重写channelRead0方法就可以实现
 */
public class MyClientHandler extends SimpleChannelInboundHandler<String> {


    /**
     * 与服务端不同 客户端主要负责读取服务端发来的数据
     * 然后显示在控制台上即可
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg.trim());
    }

    /**
     * 当发生异常时 关闭当前通道上下文对象
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
