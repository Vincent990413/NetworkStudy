package com.netty.groupchat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * 这个是服务端的入站处理器 当数据从客户端发送进入服务端的 Pipeline 时
 * <p>
 * 并且规定了数据的类型是String类型 重写channelRead0方法就可以实现
 */
public class MyServerHandler extends SimpleChannelInboundHandler<String> {

    //用于维护所有的Channel 便于转发消息
    //GlobalEventExecutor.INSTANCE 是一个全局事件执行器 并且是单例的
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //记录关于时间的操作
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 一旦连接 则第一个执行这个方法 我们需要在上线（连接成功）同时 立即加入到所维护的channels中
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("线程信息为：" + Thread.currentThread().getName());

        Channel channel = ctx.channel();
        //将该客户加入系统的信息 推送给其他在线的客户端

        //这个方法 会自身循环所有的channel 给所有的channel发送一遍
        channels.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天\n");
        channels.add(channel);
        super.handlerAdded(ctx);
    }

    /**
     * 处于活动状态 此时在服务端内部可以记录 某某某上线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了~");
        super.channelActive(ctx);
    }

    /**
     * 处于已经断开状态 将离开的信息 推送给当前在线的客户
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.writeAndFlush("[客户端]" + channel.remoteAddress() + "离开了\n");

        System.out.println("当前群聊还剩下" + channels.size() + "个人！");
        super.handlerRemoved(ctx);
    }

    /**
     * 处于非活动状态 此时可以在服务端内部记录 某某某离线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "下线了~");
        super.channelInactive(ctx);
    }

    /**
     * 通道读取信息的处理 服务端会接收到各个客户端的消息
     * 作为群发系统 我们自然是要维护一个Channel组 管理所有的Channel
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        //获取到当前channel
        Channel channel = ctx.channel();

        //此时需要遍历channels 注意 不能群发给自己 所以要分情况讨论
        channels.forEach(currentChannel -> {
            //如果是非自身Channel的通道 可以转发
            if (channel != currentChannel) {
                currentChannel.writeAndFlush("[客户端]" + channel.remoteAddress() + "发送了消息：" + msg + "\n");
            } else {
                //如果是自己 不用回显 没必要
            }
        });
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
