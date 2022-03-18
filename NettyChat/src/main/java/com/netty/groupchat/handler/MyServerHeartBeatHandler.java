package com.netty.groupchat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这个是服务端的入站处理器 当数据从客户端发送进入服务端的 Pipeline 时
 * <p>
 * 并且规定了数据的类型是String类型 重写channelRead0方法就可以实现
 */
public class MyServerHeartBeatHandler extends SimpleChannelInboundHandler<String> {

    private AtomicInteger readIdleTimes = new AtomicInteger(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println(" ====== > [server] message received : " + s);
        if ("I am alive".equals(s)) {
            ctx.channel().writeAndFlush("copy that");
        } else {
            System.out.println("其他信息处理 ... ");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt; //空闲类型分为读操作空闲、写操作空闲以及读写操作空闲

        String eventType = null;
        switch (event.state()) {
            case READER_IDLE:
                eventType = "读空闲";
                // 读空闲的计数加1
                readIdleTimes.incrementAndGet();
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                // 不处理
                break;
            case ALL_IDLE:
                eventType = "读写空闲";
                // 不处理
                break;
        }
        System.out.println(ctx.channel().remoteAddress() + "超时事件：" + eventType);
        if (readIdleTimes.get() > 3) {
            System.out.println(" [server]读空闲超过3次，关闭连接");
            ctx.channel().writeAndFlush("you are out");
            ctx.channel().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("=== " + ctx.channel().remoteAddress() + " is active ===");
    }
}
