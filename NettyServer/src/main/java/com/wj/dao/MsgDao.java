package com.wj.dao;

import com.wj.bean.Offline_Msg;

import java.util.List;

public interface MsgDao {
    boolean insertMsg(String to_name, String from_name, String msg,int msg_type,int flag);

    List<Offline_Msg> getMsg(String name);

    boolean deleteMsg(String name);
}
