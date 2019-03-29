package com.wj.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wj.contant.MsgType;
import com.wj.service.DispatchService;
import com.wj.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ServerHandler extends SimpleChannelInboundHandler<String> {
    /**
     * 统一处理用户json的控制器
     */
    private DispatchService ds;

    public ServerHandler() {
        ds = DispatchService.getInstance();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object s) throws Exception {

        /**
         * 处理消息
         */
        ObjectNode sendnode = ds.process(channelHandlerContext,s);
        /**
         * 向服务端返回结果信息
         */
        String loginmsg = sendnode.toString();
        ByteBuf buffer = Unpooled.buffer(1024);
        buffer.writeBytes(loginmsg.getBytes());
        /**
         * 向服务端发送消息
         */
        System.out.println(channelHandlerContext.channel().remoteAddress()+"发送消息");
        channelHandlerContext.writeAndFlush(buffer);

        System.out.println("发送成功");

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s)  {

    }

    /**
     * 客户端上线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * 下线抛异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*System.out.println("异常下线"+cause.getLocalizedMessage());
        super.exceptionCaught(ctx, cause);
        ds.offlineInfo(ctx);*/

    }

    /**
     * 客户端下线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);

        System.out.println("正常下线");
        ds.offlineInfo(ctx);

    }
}
