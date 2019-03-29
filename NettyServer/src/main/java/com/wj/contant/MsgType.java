package com.wj.contant;

public enum MsgType {

        EN_MSG_LOGIN, //用户登录消息
        EN_MSG_LOGIN_ACK, //用户的登录响应消息
        EN_MSG_REGISTER, //用户注册消息
        EN_MSG_REGISTER_ACK, //用户注册响应消息
        EN_MSG_FORGET_PWD, //用户忘记密码消息
        EN_MSG_FORGET_PWD_ACK, //用户忘记密码响应消息
        EN_MSG_MODIFY_PWD, //修改密码休息
        EN_MSG_MODIFY_PWD_ACK,
        EN_MSG_CHAT, //一对一聊天消息
        EN_MSG_CHAT_ACK, //一对一聊天响应消息
        EN_MSG_CHAT_ALL, //群聊消息
        EN_MSG_NOTIFY_ONLINE, //群发用户上线消息
        EN_MSG_NOTIFY_OFFLINE, //群发用户下线消息
        EN_MSG_OFFLINE, //用户下线消息
        EN_MSG_OFFLINE_ACK,//用户下线反馈消息
        EN_MSG_GET_ALL_USERS, //获取所有在线用户信息
        EN_MSG_TRANSFER_FILE, //传输文件消息
        EN_MSG_TRANSFER_FILE_ACK,//传输文件消息回复相应
        EN_MSG_TRANSFER_FILE_RECEV, //接受方传输文件消息【新增】
        EN_MSG_CHECK_USER_EXIST, //用户是否存在【新增】
        EN_MSG_OFFLINE_MSG_EXIST, //是否存在离线消息【新增】
        EN_MSG_OFFLINE_FILE_EXIST, //是否存在离线文件【新增】


}
