package com.wj.service.impl;

import com.wj.bean.User;
import com.wj.dao.UserDao;
import com.wj.dao.impl.UserDaoImpl;
import com.wj.service.UserService;
import com.wj.util.EmailUtils;

import java.util.List;

public class UserServiceImpl implements UserService {
    UserDao userDao = new UserDaoImpl();
    @Override
    public boolean doLogin(String name, String pwd) {

        User user  = userDao.getUserInfoByNamePwd(name,pwd);
        if(user == null){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean doRegister(String name1, String pwd1, String email1) {
        Boolean flag = userDao.setUser(name1,pwd1,email1);

        return flag;
    }

    @Override
    public boolean dotoemail(String name2, String email2, int number) {
        try {
            User user  = userDao.selectByemailname(name2,email2);
            if(user == null){
                return false;
            }else {
                EmailUtils.sendEmail(email2,String.valueOf(number));
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean modifyPwd(String name3, String newpassword) {
        Boolean flag = userDao.modifyPwd(name3,newpassword);
        return flag;
    }

    @Override
    public boolean isExist(String name4) {
        User user  = userDao.getUserByname(name4);
        if(user == null){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();

    }


}