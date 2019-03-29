package com.wj.bean;

public class Offline_Msg {
    Integer id;
    //群聊和一对一的标志   1表示一对一   0表示群聊 2表示文件
    Integer user_id;
    String to_name;
    String from_name;
    Integer msg_type;
    String msg;
    Integer state;

    public Offline_Msg() {
    }

    public Offline_Msg(Integer id, Integer user_id, String to_name, String from_name, Integer msg_type, String msg, Integer state) {
        this.id = id;
        this.user_id = user_id;
        this.to_name = to_name;
        this.from_name = from_name;
        this.msg_type = msg_type;
        this.msg = msg;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Offline_Msg{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", to_name='" + to_name + '\'' +
                ", from_name='" + from_name + '\'' +
                ", msg_type=" + msg_type +
                ", msg='" + msg + '\'' +
                ", state=" + state +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public Integer getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Integer msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
