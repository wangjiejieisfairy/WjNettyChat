package com.wj.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wj.contant.MsgType;
import com.wj.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

import java.io.File;
import java.util.Scanner;

/**
 * 客户端的具体业务逻辑
 */
public class ClientController {


    private ChannelFuture future ;
    private Scanner scanner = new Scanner(System.in);

    /**
     * 记录当前用户登录信息
     *
     */
    private String loginUser;


    public ClientController(ChannelFuture future) {
        this.future = future;
    }

    /**
     * 客户端运行
     */
    public void run(){
        loginShow();
        System.out.println("请选择：");
        String line = scanner.nextLine();
        try{
            Integer chioce = Integer.valueOf(line);
            switch (chioce) {
                case 1:
                    //登录
                    doLogin();
                    break;
                case 2:
                    //注册
                    doRegister();
                    break;
                case 3:
                    //忘记密码
                    forgetPasword();
                    break;
                case 4:
                    //退出系统
                    System.exit(0);
                    break;
                default:
                    System.out.println("请您输入正确的好吗？亲！");
            }

        }catch (Exception e){
            System.out.println("请您输入正确的好吗？亲！");
            run();
        }

    }

    private void forgetPasword(){
        System.out.println("请输入你的用户名");
        String name = scanner.nextLine();
        System.out.println("请输入您注册的邮箱");
        String email = scanner.nextLine();
        /**
         * 封装发送给服务器端的数据
         */
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("name",name);
        node.put("email",email);
        node.put("type",String.valueOf(MsgType. EN_MSG_FORGET_PWD));
        /**
         * 发送给服务器
         */
        String forgetmsg = node.toString();
        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes(forgetmsg.getBytes());
        future.channel().writeAndFlush(byteBuf);

        System.out.println("发送忘记密码消息成功");
        int flag = 0;
        try{
            flag =  ClientHandler.queue.take();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("验证码为"+flag);
        /**
         * 判断用户名与邮箱是否匹配
         */
        if(flag == 1){
            /**
             * 服务端返回注册成功失败状态码
             * 子线程接收消息，返回主线程状态码
             * 用一个阻塞队列 ---》queue.take()
             */
            System.out.println("请输入你的验证码");
            String captcha = scanner.nextLine();

            Integer number = 0;
            try{
                number = (Integer)ClientHandler.queue.take();

            }catch (Exception e) {
                e.printStackTrace();
            }

            //判断输入的验证码是否正确
            if((int)number == (int)Integer.parseInt(captcha)){
                System.out.println("请输入新密码");
                String newpassword = scanner.nextLine();
                ObjectNode node1 = JsonUtil.getObjectNode();
                /**
                 * 封装发送给服务器端的数据
                 */
                node1 = JsonUtil.getObjectNode();
                node1.put("name",name);
                node1.put("newpassword",newpassword);
                node1.put("type",String.valueOf(MsgType. EN_MSG_MODIFY_PWD));
                /**
                 * 发送给服务器
                 */
                String modifymsg = node1.toString();
                ByteBuf byteBuf1 = Unpooled.buffer(1024);
                byteBuf1.writeBytes(modifymsg.getBytes());
                future.channel().writeAndFlush(byteBuf1);
                System.out.println("发送修改密码消息成功");
                int modifyflag = 0;
                try{
                    modifyflag =  ClientHandler.queue.take();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                if(modifyflag == 1){
                    System.out.println("修改成功");
                    run();
                }else {
                    System.out.println("修改失败");
                    run();
                }

            }else {
                System.out.println("验证码错误");

                run();
            }

        }else {
            System.out.println("用户名邮箱不匹配错误");
            ClientHandler.queue.clear();
            run();
        }




    }
    private void doRegister(){
        System.out.println("请输入用户名");
        String name = scanner.nextLine();
        System.out.println("请输入密码");
        String pwd = scanner.nextLine();
        System.out.println("请输入邮箱");
        String email = scanner.nextLine();

        /**
         * 封装发送给服务器端的数据
         */
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("name",name);
        node.put("pwd",pwd);
        node.put("email",email);
        node.put("type",String.valueOf(MsgType. EN_MSG_REGISTER));
        /**
         * 发送给服务器
         */
        String registermsg = node.toString();
        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes(registermsg.getBytes());
        future.channel().writeAndFlush(byteBuf);

        System.out.println("发送注册消息成功");
        /**
         * 服务端返回注册成功失败状态码
         * 子线程接收消息，返回主线程状态码
         * 用一个阻塞队列 ---》queue.take()
         */
        int registerflag = 0;
        try{
            registerflag =  ClientHandler.queue.take();
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(registerflag == 1){
            System.out.println("注册成功");
            run();
        }else {
            System.out.println("用户名已存在");
            run();
        }
    }
    private void doLogin(){
        System.out.println("用户名：");
        String name = scanner.nextLine();
        System.out.println("密码：");
        String pwd = scanner.nextLine();
        /**
         * 封装发送给服务端的数据
         */
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", String.valueOf(MsgType.EN_MSG_LOGIN));
        node.put("name",name);
        node.put("pwd",pwd);
        String loginmsg = node.toString();

        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes(loginmsg.getBytes());

        /**
         * 向服务端发送消息
         */
        future.channel().writeAndFlush(byteBuf);
        System.out.println("发送消息成功");
        /**
         * 服务端返回登录成功失败状态码
         * 子线程接收消息，返回主线程状态码
         * 用一个阻塞队列 ---》queue.take()
         */
        int loginflag = 0;
        try{
            loginflag =  ClientHandler.queue.take();
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(loginflag == 1){
            //记录用户信息
            loginUser  = name;
            System.out.println("登录成功");
            Mainloop();
        }else {
            System.out.println("用户名密码错误");
            run();
        }
    }
    /**
     * 页面选择Show
     */
    private void loginShow(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("-----------\n");
        stringBuffer.append("1，登录\n");
        stringBuffer.append("2，注册\n");
        stringBuffer.append("3，忘记密码\n");
        stringBuffer.append("4，退出系统\n");
        stringBuffer.append("-----------\n");
        System.out.println(stringBuffer.toString());
    }

    /**
     * 主菜单循环
     */
    private void Mainloop(){
        while(true){
            showMainDemu();
            String line = scanner.nextLine();
            if("".equals(line)){
                continue;
            }
            if("quit".equals(line)){
                System.exit(0);
                return;
            }
            if("help".equals(line)){
                continue;
            }
            String[] strings = line.split(":");
            for(int i = 0;i < strings.length;i++) {
                strings[i] = strings[i].trim();
            }

            /**
             *
             */
            if(strings.length == 1){
                if(strings[0].equals("getallusers")){
                    /**
                     * 查询所有在线人员消息
                     */
                    selectOnline();
                }else {
                    System.out.println("输入不合法，重新输入");
                    continue;
                }
            }else if(strings.length == 2){
                if(strings[0].equals("all")){
                    /**
                     * 表示群聊
                     */
                    chatAll(strings[1]);
                }else if(strings[0].equals("modifypwd")){
                    /**
                     * 表示修改密码
                     */
                    modifyPwd(strings[1]);
                }else if(strings[0].equals("quit")){
                    /**
                     * 表示用户下线
                     */
                    quits(strings[1]);
                }else {

                    chat(strings[0],strings[1]);

                }

            }else if(strings.length >= 3){
                if(strings.length == 3){
                    if(strings[0].equals("sendfile")){
                        /**
                         * 发送文件请求
                         */
                        sendFile(strings[1],strings[2]);
                    }
                }else{
                    strings[2] = strings[2]+":"+strings[3];
                    if(strings[0].equals("sendfile")){
                        /**
                         * 发送文件请求
                         */
                        sendFile(strings[1],strings[2]);
                    }
                }

            }else {
                System.out.println("输入不合法，重新输入");
                continue;
            }


        }

    }

    /**
     * 表示发送文件请求
     * @param toname 接收方用户名
     * @param filepath 文件路径
     */
    private void sendFile(String toname, String filepath) {
        File file = new File(filepath);
        System.out.println("文件路径为:"+filepath);
        if(!file.exists()){
            System.out.println("文件不存在");
            return;
        }
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type",MsgType.EN_MSG_TRANSFER_FILE.toString());
        node.put("to",toname);
        String msg = node.toString();
        ByteBuf byteBuf = Unpooled.buffer(256);
        byteBuf.writeBytes(msg.getBytes());
        //发消息
        future.channel().writeAndFlush(byteBuf);

        //等待服务端返回，服务端分配的端口
        //坑：阻塞队列设计为Integer类型，服务端返回端口或者是状态码无法做到序列化
        //可以改为，在阻塞队列中封装自定义类型：包含状态码以及服务端有效信息
        int port = 0;
        try{
            port =  ClientHandler.queue.take();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("收到返回的内容");
        //发送
        FileSendHandler fileSendHandler = new FileSendHandler("127.0.0.1",port);
        fileSendHandler.sendFile(file);

    }

    /**
     * 用户下线
     * @param username 用户名
     */
    private void quits(String username) {
        ObjectNode objectNode = JsonUtil.getObjectNode();
        objectNode.put("type",MsgType.EN_MSG_OFFLINE.toString());
        objectNode.put("name",username);
        String quitmsg = objectNode.toString();
        ByteBuf byteBuf = Unpooled.buffer(256);
        byteBuf.writeBytes(quitmsg.getBytes());
        //发消息
        future.channel().writeAndFlush(byteBuf);

        int code = 0;
        //等待服务端返回的状态码
        try{
            code = ClientHandler.queue.take();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(code == 200){
            System.out.println("下线成功");
            System.exit(0);
        }else if(code == 500){
            System.out.println("输入用户不是当前用户");
        }
    }

    /**
     * 修改密码
     * @param username 用户名
     */
    private void modifyPwd(String username) {
    }

    /**
     * 查询所有在线人员信息
     */
    private void selectOnline() {

        ObjectNode objectNode = JsonUtil.getObjectNode();
        objectNode.put("type",MsgType.EN_MSG_GET_ALL_USERS.toString());

        String getuser = objectNode.toString();
        ByteBuf byteBuf = Unpooled.buffer(256);
        byteBuf.writeBytes(getuser.getBytes());
        //发消息
        future.channel().writeAndFlush(byteBuf);


    }

    /**
     * 群发消息
     * @param msg 消息
     */
    private void chatAll(String msg) {

        ObjectNode objectNode = JsonUtil.getObjectNode();
        objectNode.put("type",MsgType.EN_MSG_CHAT_ALL.toString());
        objectNode.put("msg",msg);
        objectNode.put("from_name",loginUser);

        String allchatmsg = objectNode.toString();
        ByteBuf byteBuf = Unpooled.buffer(256);
        byteBuf.writeBytes(allchatmsg.getBytes());
        //发消息
        future.channel().writeAndFlush(byteBuf);
    }

    /**
     * 一对一聊天
     * @param toName 发送方
     * @param msg 消息
     */
    private void chat(String toName,String msg){
        ObjectNode objectNode = JsonUtil.getObjectNode();
        objectNode.put("type",MsgType.EN_MSG_CHAT.toString());
        //发送方
        objectNode.put("from",loginUser);
        //要接收方
        objectNode.put("to",toName);
        //消息
        objectNode.put("msg",msg);

        String chatmsg = objectNode.toString();
        ByteBuf byteBuf = Unpooled.buffer(256);
        byteBuf.writeBytes(chatmsg.getBytes());
        //发消息
        future.channel().writeAndFlush(byteBuf);
        int code = 0;
        //等待服务端返回的状态码
        try{
            code = ClientHandler.queue.take();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(code == 200){
            System.out.println("发送成功");
        }else if(code == 500){
            System.out.println("当前用户不在线");
        }else if(code == 300) {
            System.out.println("当前用户不存在");
        }


    }
    /**
     * 主菜单
     */
    private void showMainDemu(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("==================================================\n");
        stringBuffer.append("主菜单功能介绍\n");
        stringBuffer.append("1:输入modifypwd:username表示该用户要修改密码\n");
        stringBuffer.append("2:输入getallusers:表示该用户要查询所有在线人员信息\n");
        stringBuffer.append("3:输入username:xxx 表示一对一聊天\n");
        stringBuffer.append("4:输入all:xxx 表示发送群聊消息\n");
        stringBuffer.append("4:输入sendfile:username:xxx表示发送文件请求\n");
        stringBuffer.append("4:输入quit:username 表示该用户下线，退出系统\n");
        stringBuffer.append("==================================================\n");
        System.out.println(stringBuffer.toString());

    }

}
