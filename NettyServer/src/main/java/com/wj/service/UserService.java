package com.wj.service;

import com.wj.bean.User;

import java.util.List;

public interface UserService {
    /**
     * 登录操作
     * @param name：用户名
     * @param pwd：用户密码
     * @return true：登录成功 false：登录失败
     */
    boolean doLogin(String name, String pwd);

    boolean doRegister(String name1, String pwd1, String email1);

    boolean  dotoemail(String name2, String email2, int number);

    boolean modifyPwd(String name3, String newpassword);

    boolean isExist(String name4);

    List<User> getAllUser();
}
