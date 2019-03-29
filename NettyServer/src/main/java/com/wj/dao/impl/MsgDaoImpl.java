package com.wj.dao.impl;

import com.wj.bean.Offline_Msg;
import com.wj.bean.User;
import com.wj.dao.MsgDao;
import com.wj.util.C3p0Util;

import java.lang.ref.PhantomReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MsgDaoImpl implements MsgDao {
    @Override
    public boolean insertMsg(String to_name, String from_name, String msg,int msg_type,int flags) {
        PreparedStatement preparedStatement = null;
        int flag = 0;
        try {
            System.out.println("flags为：....."+flags);
           Connection connection;

            connection = C3p0Util.getConnection();
            preparedStatement = connection.prepareStatement("insert into offline_msg(user_id,to_name,from_name,msg_type,msg,state) values (?,?,?,?,?,?)");
            preparedStatement.setInt(1,flags);//1表示一对一 0表示群聊 2表示文件
            preparedStatement.setString(2,to_name);
            preparedStatement.setString(3,from_name);
            preparedStatement.setInt(4,msg_type);
            preparedStatement.setString(5,msg);
            preparedStatement.setInt(6,1);//1,表示未读
            flag = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(flag == 0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 得到用户的离线消息
     * @param name
     * @return
     */
    @Override
    public List<Offline_Msg> getMsg(String name) {
        List<Offline_Msg> list = new ArrayList<>();
        Connection connection = null;
        try {
            connection = C3p0Util.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from offline_msg where to_name =? and state=?");
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,1);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                Offline_Msg offline_msg = new Offline_Msg();
                offline_msg.setId(resultSet.getInt("id"));
                offline_msg.setUser_id(resultSet.getInt("user_id"));
                offline_msg.setTo_name(resultSet.getString("to_name"));
                offline_msg.setFrom_name(resultSet.getString("from_name"));
                offline_msg.setMsg_type(resultSet.getInt("msg_type"));
                offline_msg.setMsg(resultSet.getString("msg"));
                offline_msg.setState(resultSet.getInt("state"));
                list.add(offline_msg);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteMsg(String name) {

        Connection connection = null;
        int flag = 0;
        try {
            connection = C3p0Util.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("update  offline_msg set state=? where to_name =? ");
            preparedStatement.setInt(1,0);
            preparedStatement.setString(2,name);
            flag = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if(flag == 0){
            return false;
        }else {
            return true;
        }

    }


}
