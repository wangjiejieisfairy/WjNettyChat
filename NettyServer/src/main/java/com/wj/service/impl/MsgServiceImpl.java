package com.wj.service.impl;

import com.wj.bean.Offline_Msg;
import com.wj.dao.MsgDao;
import com.wj.dao.impl.MsgDaoImpl;
import com.wj.service.MsgService;

import java.util.List;

public class MsgServiceImpl implements MsgService {
    MsgDao msgDao = new MsgDaoImpl();
    @Override
    public boolean insertMsg(String to_name, String from_name, String msg,int msg_type,int flag) {
        boolean flags = msgDao.insertMsg(to_name,from_name,msg,msg_type,flag);
        return flags;
    }

    @Override
    public List<Offline_Msg> getMsg(String name) {
        List<Offline_Msg> offline_msgs = msgDao.getMsg(name);

        return offline_msgs;
    }

    @Override
    public boolean deleteMsg(String name) {
        boolean flag= msgDao.deleteMsg(name);
        return flag;
    }
}
