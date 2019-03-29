package com.wj.dao.impl;

import com.wj.bean.User;
import com.wj.dao.UserDao;
import com.wj.util.C3p0Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserDaoImpl implements UserDao {
    Connection connection = null;

    @Override
    public User getUserInfoByNamePwd(String name, String pwd) {
        try {
            connection = C3p0Util.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where name =? and pwd=?");
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,pwd);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPwd(resultSet.getString("pwd"));
                user.setEmail(resultSet.getString("email"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            /*if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        }
        return null;
    }

    @Override
    public Boolean setUser(String name1, String pwd1, String email1) {

        PreparedStatement preparedStatement = null;
        int flag = 0;
        try {
            Connection connection1 = C3p0Util.getConnection();
            PreparedStatement preparedStatement1 = connection1.prepareStatement("select * from user where name=?");
            preparedStatement1.setString(1,name1);
            ResultSet resultSet = preparedStatement1.executeQuery();
            if(resultSet.next()){
                return false;
            }

            connection = C3p0Util.getConnection();
            preparedStatement = connection.prepareStatement("insert into user(name,pwd,email) values (?,?,?)");
            preparedStatement.setString(1,name1);
            preparedStatement.setString(2,pwd1);
            preparedStatement.setString(3,email1);
            flag = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(flag != 0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean modifyPwd(String name3, String newpassword) {
        PreparedStatement preparedStatement = null;
        int flag = 0;
        try {
            connection = C3p0Util.getConnection();
            preparedStatement = connection.prepareStatement("update user set pwd=? where name = ?");
            preparedStatement.setString(1,newpassword);
            preparedStatement.setString(2,name3);

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

    @Override
    public User selectByemailname(String name2, String email2) {

        try {
            connection = C3p0Util.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where name =? and email=?");
            preparedStatement.setString(1,name2);
            preparedStatement.setString(2,email2);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPwd(resultSet.getString("pwd"));
                user.setEmail(resultSet.getString("email"));
                return user;
            }

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
    public User getUserByname(String name4) {


        try {
            connection = C3p0Util.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where name =? ");
            preparedStatement.setString(1,name4);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPwd(resultSet.getString("pwd"));
                user.setEmail(resultSet.getString("email"));
                return user;
            }

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
    public List<User> getAllUser() {
        List<User> list = new ArrayList<>();
        try {

            connection = C3p0Util.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user ");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPwd(resultSet.getString("pwd"));
                user.setEmail(resultSet.getString("email"));
                list.add(user);
            }

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
        return list;
    }
}
