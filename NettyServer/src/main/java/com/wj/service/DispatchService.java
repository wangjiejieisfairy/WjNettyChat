package com.wj.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wj.bean.Offline_Msg;
import com.wj.bean.User;
import com.wj.contant.MsgType;
import com.wj.controller.FileHandler;
import com.wj.dao.MsgDao;
import com.wj.dao.UserDao;
import com.wj.dao.impl.UserDaoImpl;
import com.wj.service.impl.MsgServiceImpl;
import com.wj.service.impl.UserServiceImpl;
import com.wj.util.JsonUtil;
import com.wj.util.PortUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用单例模式 接收所有服务，并做分发处理
 */
public class DispatchService {
    //记录用户名和通道的对应关系，在服务端转发消息时使用
    private static ConcurrentHashMap<String,ChannelHandlerContext> onlineNameChannel =
            new ConcurrentHashMap<>();

    //记录用户名的channel和我们的用户名的对应关系，在用户上下线使用  主要是异常时
    private static ConcurrentHashMap<ChannelHandlerContext,String> onlineChannelName =
            new ConcurrentHashMap<>();
    //用户服务
    private UserService userService = new UserServiceImpl();

    //离线消息
    private MsgService msgService = new MsgServiceImpl();
    private static volatile DispatchService dispatchService = null;
    private DispatchService() {

    }

    /**
     * 双层校验锁
     * @return
     */
    public static DispatchService getInstance(){
        Object object = new Object();
        if(dispatchService == null){
            synchronized (DispatchService.class) {
                if(dispatchService == null){
                    dispatchService = new DispatchService();
                }
            }
        }
        return dispatchService;
    }

    /**
     * 注册上线信息
     * @param ext
     * @param name
     */
    public void onlineInfo(ChannelHandlerContext ext,String name){
        System.out.println("登錄註冊信息");
        /**
         * 上线后，获取当前用户的离线消息
         */
        List<Offline_Msg> offline_msgList = msgService.getMsg(name);
        if(offline_msgList .size() != 0){
            /**
             * 遍历所有的离线消息
             */
            String from_name = "";
            String msg = "";
            String flag = "";
            for(int i = 0;i < offline_msgList.size();i++) {
                //如果该消息是文件的话
                if(offline_msgList.get(i).getUser_id() == 2){
                    from_name = offline_msgList.get(i).getFrom_name();
                    //接收方端口
                    int tport = PortUtils.getFreePort();
                    ObjectNode objectNode = JsonUtil.getObjectNode();
                    objectNode.put("type",String.valueOf(MsgType.EN_MSG_TRANSFER_FILE));
                    objectNode.put("code",600);//状态码600，表示是离线文件消息
                    objectNode.put("port",tport);



                    String tomsg = objectNode.toString();
                    ByteBuf buffer = Unpooled.buffer(1024);
                    buffer.writeBytes(tomsg.getBytes());
                    ext.channel().writeAndFlush(buffer);
                    System.out.println("给接收方分配端口成功 ");
                    //文件在服务器中的存储路径
                    msg = offline_msgList.get(i).getMsg();

                    FileHandler fileHandler = new FileHandler(0,tport,from_name,name,msg);
                    new Thread(fileHandler).start();
                    break;
                }else {
                    from_name = offline_msgList.get(i).getFrom_name()+":"+from_name;
                    msg = offline_msgList.get(i).getMsg()+":"+msg;
                    flag = String.valueOf(offline_msgList.get(i).getUser_id())+":"+flag;
                }

            }



            ObjectNode objectNode = JsonUtil.getObjectNode();
            objectNode.put("type", MsgType.EN_MSG_OFFLINE_MSG_EXIST.toString());
            objectNode.put("from_name",from_name);
            objectNode.put("msg",msg);
            objectNode.put("flag",flag);
            objectNode.put("code",200);//表示是离线消息

            String offlinemsg = objectNode.toString();
            ByteBuf buffer = Unpooled.buffer(1024);
            buffer.writeBytes(offlinemsg.getBytes());
            /**
             * 向服务端发送消息
             */
            ext.writeAndFlush(buffer);
            //删除用户的离线消息
            boolean flags = msgService.deleteMsg(name);
        }


        /**
         * 遍历所有在线用户，并给它们发送用户上线消息
         */

        Iterator<Map.Entry<ChannelHandlerContext,String>> iterator
                = onlineChannelName.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<ChannelHandlerContext,String> next = iterator.next();
            ObjectNode objectNode = JsonUtil.getObjectNode();
            objectNode.put("type",MsgType.EN_MSG_NOTIFY_ONLINE.toString());
            String msg = name+"上线了！";
            objectNode.put("msg",msg);
            String onlinemsg = objectNode.toString();
            ByteBuf buffer = Unpooled.buffer(1024);
            buffer.writeBytes(onlinemsg.getBytes());
            /**
             * 向服务端发送消息
             */
            next.getKey().writeAndFlush(buffer);
        }
        onlineNameChannel.put(name,ext);
        onlineChannelName.put(ext,name);


    }

    /**
     * 删除下线用户信息
     * @param ext
     */
    public void  offlineInfo(ChannelHandlerContext ext){
        String name = onlineChannelName.get(ext);
        onlineChannelName.remove(ext);
        if(!(name.equals("") || name == null)){
            onlineNameChannel.remove(name);
        }
        /**
         * 遍历所有在线用户，并给它们发送用户下线消息
         */
        Iterator<Map.Entry<ChannelHandlerContext,String>> iterator
                = onlineChannelName.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<ChannelHandlerContext,String> next = iterator.next();
            ObjectNode objectNode = JsonUtil.getObjectNode();
            objectNode.put("type",MsgType.EN_MSG_NOTIFY_OFFLINE.toString());
            String msg = name+"下线了！";
            objectNode.put("msg",msg);

            String offlinemsg = objectNode.toString();
            ByteBuf buffer = Unpooled.buffer(1024);
            buffer.writeBytes(offlinemsg.getBytes());
            /**
             * 向服务端发送消息
             */
            next.getKey().writeAndFlush(buffer);
        }

    }

    public void offlineInfo(String name){
        ChannelHandlerContext context = onlineNameChannel.get(name);
        onlineChannelName.remove(context);
        onlineNameChannel.remove(name);
    }

    /**
     * 处理离线消息
     * @param to_name 接收方
     * @param from_name 发送方
     * @param msg 消息
     * @param flag 群聊或者一对一的标志
     */
    public void hander(String to_name, String from_name, String msg,int msg_type,int flag) {
        msgService.insertMsg(to_name, from_name,  msg,msg_type,flag);
    }


    public ObjectNode process(ChannelHandlerContext channelHandlerContext, Object s){
        ObjectNode sendnode = null;
        /**
         * 拿到客户端发来的数据
         */
        String string  = s.toString();
        ObjectNode node = JsonUtil.getObjectNode(string);
        /**
         * 拿到json数据的type类型EN_MSG_LOGIN
         */
        String type = String.valueOf(node.get("type").asText());
        switch (type) {
            case "EN_MSG_LOGIN":
                sendnode = JsonUtil.getObjectNode();
                sendnode.put("type",String.valueOf(MsgType.EN_MSG_LOGIN_ACK));
                /**
                 * 判断登录是否成功
                 */
                String name = String.valueOf(node.get("name").asText());
                String pwd = String.valueOf(node.get("pwd").asText());
                boolean login = userService.doLogin(name, pwd);
                if(login){
                    //注册登录信息
                    onlineInfo(channelHandlerContext,name);
                    sendnode.put("flag",1);
                }else {
                    sendnode.put("flag",0);
                }

                break;
            case "EN_MSG_REGISTER":
                sendnode = JsonUtil.getObjectNode();
                sendnode.put("type",String.valueOf(MsgType.EN_MSG_REGISTER_ACK));
                /**
                 * 判断注册是否成功
                 */
                String name1 = String.valueOf(node.get("name").asText());
                String pwd1 = String.valueOf(node.get("pwd").asText());
                String email1 = String.valueOf(node.get("email").asText());

                boolean register = userService.doRegister(name1,pwd1,email1);

                if(register){
                    sendnode.put("flag",1);
                }else {
                    sendnode.put("flag",0);
                }
                break;
            case "EN_MSG_FORGET_PWD" :
                sendnode = JsonUtil.getObjectNode();
                sendnode.put("type",String.valueOf(MsgType.EN_MSG_FORGET_PWD_ACK));
                /**
                 * 判断邮箱发送是否成功
                 */
                String name2 = String.valueOf(node.get("name").asText());
                String email2 = String.valueOf(node.get("email").asText());
                Random random = new Random();
                int number = random.nextInt(1000);
                boolean mail = userService.dotoemail(name2,email2,number);
                sendnode.put("number",number);
                if(mail){
                    sendnode.put("flag",1);
                }else {
                    sendnode.put("flag",0);
                }
                break;
            case "EN_MSG_MODIFY_PWD":
                sendnode = JsonUtil.getObjectNode();
                sendnode.put("type",String.valueOf(MsgType.EN_MSG_MODIFY_PWD_ACK));
                /**
                 * 判断修改密码是否成功
                 */
                String name3 = String.valueOf(node.get("name").asText());
                String newpassword = String.valueOf(node.get("newpassword").asText());
                boolean flag = userService.modifyPwd(name3,newpassword);
                if(flag){
                    sendnode.put("flag",1);
                }else {
                    sendnode.put("flag",0);
                }
                break;

            case "EN_MSG_CHAT":
                sendnode = JsonUtil.getObjectNode();
                /**
                 * 一对一聊天
                 */
                /**
                 * 先判断当前用户是否存在
                 * 判断用户是否在线
                 * 在线则直接转发消息
                 */
                String name4 = String.valueOf(node.get("to").asText());
                //判断用户是否存在
                boolean exist = userService.isExist(name4);
                if(!exist){
                    //用户不存在
                    sendnode.put("type",String.valueOf(MsgType.EN_MSG_CHAT_ACK));
                    sendnode.put("code",300);//状态码为300，表示不存在这个用户
                    return sendnode;
                }
                boolean exit = onlineNameChannel.containsKey(name4);

                if(!exit){
                    String to_name = String.valueOf(node.get("to").asText());
                    String from_name = String.valueOf(node.get("from").asText());
                    String msg =String.valueOf(node.get("msg").asText());

                    /**
                     * 处理离线消息
                     */
                    hander(to_name,from_name,msg,1,1);


                    //不在线
                    sendnode.put("type",String.valueOf(MsgType.EN_MSG_CHAT_ACK));
                    sendnode.put("code",500);//状态码为500，表示当前用户不在线
                    return sendnode;
                }


                //消息转发 拿到对应的通道
                ChannelHandlerContext toNamehandler = onlineNameChannel.get(name4);
                //转发消息
                toNamehandler.channel().writeAndFlush(s);


                sendnode.put("type",String.valueOf(MsgType.EN_MSG_CHAT_ACK));
                sendnode.put("code",200);//状态码为200，表示发送成功
                break;
            case "EN_MSG_OFFLINE":
                sendnode = JsonUtil.getObjectNode();

                String name5 = String.valueOf(node.get("name").asText());
                //判断用户和通道是否相同
                if(onlineChannelName.get(channelHandlerContext).equals(name5)){
                    /**
                     * 用户下线操作
                     */
                    offlineInfo(channelHandlerContext);
                    sendnode.put("type",String.valueOf(MsgType.EN_MSG_OFFLINE_ACK));
                    sendnode.put("code",200);//状态码为200，表示发送成功
                    break;
                }else {
                    sendnode.put("type",String.valueOf(MsgType.EN_MSG_OFFLINE_ACK));
                    sendnode.put("code",500);//状态码为300，表示判断用户和通道不相同
                    return sendnode;
                }


            case "EN_MSG_GET_ALL_USERS":
                /**
                 * 获取在线人员信息
                 */
                sendnode = JsonUtil.getObjectNode();
                sendnode.put("type",String.valueOf(MsgType.EN_MSG_GET_ALL_USERS));
                String names = "";
                Iterator<Map.Entry<String,ChannelHandlerContext>>iterator=
                        onlineNameChannel.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String,ChannelHandlerContext> next = iterator.next();
                    names = next.getKey()+":"+names;
                }
                sendnode.put("names",names);

                break;
            case "EN_MSG_CHAT_ALL":
                sendnode = JsonUtil.getObjectNode();

                String msg = node.get("msg").asText();
                List<User> userList = userService.getAllUser();
                for(int i = 0;i < userList.size();i++) {

                    if(onlineNameChannel.containsKey(userList.get(i).getName())){
                        //转发消息
                        onlineNameChannel.get(userList.get(i).getName()).channel().writeAndFlush(s);
                    }else {
                        String from_name = onlineChannelName.get(channelHandlerContext);
                        //处理离线消息
                        hander(userList.get(i).getName(),from_name,msg,1,0);
                    }
                }

                break;
            case "EN_MSG_TRANSFER_FILE":
                System.out.println("我走到这块了啊 啊啊 啊啊啊啊");
                //接收的是一对一发送文件
                sendnode = JsonUtil.getObjectNode();
                //接收方的名字
                String toRecvName = node.get("to").asText();

                //判断用户是否存在
                boolean exist1 = userService.isExist(toRecvName);
                if(!exist1){
                    //用户不存在
                    sendnode.put("type",String.valueOf(MsgType.EN_MSG_TRANSFER_FILE));
                    sendnode.put("code",300);//状态码为300，表示不存在这个用户
                    return sendnode;
                }

                boolean exit1 = onlineNameChannel.containsKey(toRecvName);
                //发送方名字
                String f_name = onlineChannelName.get(channelHandlerContext);
                if(!exit1){
                    System.out.println("接收方不在线啦啦啦啦啦啦啦啦");
                    //接收方不在线
                    sendnode.put("type",String.valueOf(MsgType.EN_MSG_TRANSFER_FILE));
                    sendnode.put("code",500);//状态码为500，表示当前用户不在线
                    System.out.println("返回当前用户不在线");

                    //存放离线文件在服务器中
                    int fport = PortUtils.getFreePort();
                    ObjectNode objectNode = JsonUtil.getObjectNode();

                    objectNode.put("type",String.valueOf(MsgType.EN_MSG_TRANSFER_FILE_ACK));
                    objectNode.put("port",fport);
                    objectNode.put("code",200);
                    String tomsg = objectNode.toString();
                    ByteBuf buffer = Unpooled.buffer(1024);
                    buffer.writeBytes(tomsg.getBytes());
                    channelHandlerContext.channel().writeAndFlush(buffer);
                    System.out.println("给发送方分配端口成功");

                    FileHandler fileHandler = new FileHandler(fport,0,f_name,toRecvName,"null");
                    new Thread(fileHandler).start();
                    break;
                }

                //分配端口,分别给发送方和接收方分配端口

                int fport = PortUtils.getFreePort();
                int tport = PortUtils.getFreePort();
                System.out.println("准备分配端口。。。。");
                ChannelHandlerContext toRecvNamehandler = onlineNameChannel.get(toRecvName);
                ObjectNode objectNode = JsonUtil.getObjectNode();

                objectNode.put("type",String.valueOf(MsgType.EN_MSG_TRANSFER_FILE));
                objectNode.put("code",200);//状态码为200，表示成功
                objectNode.put("port",tport);

                String tomsg = objectNode.toString();
                ByteBuf buffer = Unpooled.buffer(1024);
                buffer.writeBytes(tomsg.getBytes());
                toRecvNamehandler.channel().writeAndFlush(buffer);
                System.out.println("给接收方分配端口成功 ");
                //给发送方返回消息，将端口分配给发送方

                sendnode.put("type",String.valueOf(MsgType.EN_MSG_TRANSFER_FILE_ACK));
                sendnode.put("port",fport);
                sendnode.put("code",200);

                System.out.println("给发送方分配端口成功");


                FileHandler fileHandler = new FileHandler(fport,tport,f_name,toRecvName,"null");
            new Thread(fileHandler).start();
            break;
            default:
                break;

        }

        return sendnode;
    }

}
