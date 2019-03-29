package com.wj.service;

import com.wj.bean.Offline_Msg;

import java.util.List;

public interface MsgService {
    /**
     * 设置离线消息
     * @param to_name
     * @param from_name
     * @param msg
     * @param msg_type
     * @return
     */
    boolean insertMsg(String to_name, String from_name, String msg,int msg_type,int flag);

    /**
     * 得到用户的离线消息
     * @param name
     * @return
     */
    List<Offline_Msg> getMsg(String name);

    boolean deleteMsg(String name);
}
