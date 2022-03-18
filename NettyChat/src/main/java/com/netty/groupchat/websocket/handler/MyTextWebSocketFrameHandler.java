package com.netty.groupchat.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.text.SimpleDateFormat;

//这里 TextWebSocketFrame 类型，表示一个文本帧（frame）
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 当服务器收到消息后
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息" + msg.text());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(System.currentTimeMillis());

        //回复消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器在" + date + "收到消息！"));
    }

    /**
     * 当 web 客户端连接后 触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded 被" + ctx.channel().id().asLongText() + "调用！");
        super.handlerAdded(ctx);
    }

    /**
     * 当 web 客户端断开连接后
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved 被" + ctx.channel().id().asLongText() + "调用！");
        super.handlerRemoved(ctx);
    }

    /**
     * 当异常发生时
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生！" + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}