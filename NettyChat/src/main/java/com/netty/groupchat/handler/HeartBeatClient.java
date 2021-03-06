package com.netty.groupchat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HeartBeatClient {


    static class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(" client received :" + msg);
            if (msg != null && msg.equals("you are out")) {
                System.out.println(" server closed connection , so client will close too");
                ctx.channel().closeFuture();
            }
        }
    }


}
