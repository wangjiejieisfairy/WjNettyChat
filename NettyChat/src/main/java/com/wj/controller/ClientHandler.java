package com.wj.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wj.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ArrayBlockingQueue;


public class ClientHandler extends SimpleChannelInboundHandler<String>{
    public static ArrayBlockingQueue<Integer> queue  = new ArrayBlockingQueue<Integer>(10);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object s) throws Exception {

        System.out.println(channelHandlerContext.channel().remoteAddress()+"客户端接受消息");
        String string = s.toString();
        ObjectNode node = JsonUtil.getObjectNode(string);
        /**
         * 拿到返回的数据类型
         */
        String type = node.get("type").asText();
        switch (type) {
            case "EN_MSG_LOGIN_ACK":
                queue.add( node.get("flag").asInt());
                break;
            case "EN_MSG_REGISTER_ACK":
                queue.add(node.get("flag").asInt());
                break;
            case "EN_MSG_FORGET_PWD_ACK":

                queue.add(node.get("flag").asInt());
                queue.add(node.get("number").asInt());
                break;
            case "EN_MSG_MODIFY_PWD_ACK":
                queue.add(node.get("flag").asInt());
                break;
            case "EN_MSG_CHAT" ://一对一聊天
                String sendName =  node.get("from").asText();
                String text = node.get("msg").asText();
                System.out.println(sendName+" [一对一]" +text);
                break;
            case "EN_MSG_CHAT_ACK" ://表示一对一聊天响应
                queue.add(node.get("code").asInt());
                break;
            case "EN_MSG_NOTIFY_ONLINE" :
                String text1 = node.get("msg").asText();
                System.out.println(text1);
                break;
            case "EN_MSG_NOTIFY_OFFLINE"  :
                String text2 = node.get("msg").asText();
                System.out.println(text2);
                break;
            case "EN_MSG_OFFLINE_ACK":
                queue.add(node.get("code").asInt());
                break;
            case "EN_MSG_OFFLINE_MSG_EXIST"://离线消息
                if(node.get("code").asInt() == 200){
                    String[] name = node.get("from_name").asText().split(":");
                    String[] msg = node.get("msg").asText().split(":");
                    String[] flag = node.get("flag").asText().split(":");
                    for(int i = 0;i < name.length;i++) {
                        if(Integer.parseInt(flag[i]) == 1){
                            System.out.println(name[i]+" [离线一对一]" +msg[i]);
                        }else {
                            System.out.println(name[i]+" [群聊]" +msg[i]);
                        }
                    }
                    break;
                }else {
                    //离线文件消息
                    //获取端口号
                    int port = node.get("port").asInt();
                    System.out.println("接收方收到端口");
                    //进行文件接收
                    FileRecvHandler recvHandler = new FileRecvHandler("127.0.0.1", port);
                    recvHandler.recv();
                    break;
                }

            case "EN_MSG_GET_ALL_USERS":
                String[] names = node.get("names").asText().split(":");
                System.out.println("在线人员为：");
                for(int i = 0;i < names.length;i++) {
                    System.out.printf(names[i]+" ");
                }
                System.out.println();
            case "EN_MSG_CHAT_ALL":
                String fromName =  node.get("from_name").asText();
                String msg1 = node.get("msg").asText();
                System.out.println(fromName+" [群聊]" +msg1);
                break;
            case "EN_MSG_TRANSFER_FILE"://接收方接收到要收文件的消息
                if(node.get("code").asInt() == 200){
                    //获取端口号
                    int port = node.get("port").asInt();
                    System.out.println("接收方收到端口");
                    //进行文件接收
                    FileRecvHandler recvHandler = new FileRecvHandler("127.0.0.1", port);
                    recvHandler.recv();
                    break;
                }else{
                    int code = node.get("code").asInt();
                    if(code == 500){
                        System.out.println("此用户不在线");
                        break;
                    }else {
                        System.out.println("此用户不存在");
                        break;
                    }
                }

            case "EN_MSG_TRANSFER_FILE_ACK"://服务端返回给发送方的端口
                //获取端口号
                int code = node.get("code").asInt();
                int port1 = node.get("port").asInt();
                System.out.println("发送方收到端口");
                //进行文件发送
                queue.add(port1);
                break;
            default:
                 break;
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端下线");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端上线");
    }
}
