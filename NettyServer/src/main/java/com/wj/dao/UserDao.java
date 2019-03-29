package com.wj.dao;

import com.wj.bean.User;

import java.util.List;

public interface UserDao {
    /**
     * 登录
     * @param name 用户名
     * @param pwd 密码
     * @return
     */
    User getUserInfoByNamePwd(String name, String pwd);

    /**
     * 注册
     * @param name1 用户名
     * @param pwd1 密码
     * @param email1 邮箱
     * @return
     */
    Boolean setUser(String name1, String pwd1, String email1);

    /**
     * 修改密码
     * @param name3 用户名
     * @param newpassword 新密码
     * @return
     */
    Boolean modifyPwd(String name3, String newpassword);

    /**
     * 忘记密码->查询用户名邮箱是否匹配
     * @param name2 用户名
     * @param email2 邮箱
     * @return
     */
    User selectByemailname(String name2, String email2);

    User getUserByname(String name4);

    List<User> getAllUser();
}
