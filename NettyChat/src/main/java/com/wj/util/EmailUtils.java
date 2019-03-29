package com.wj.util;

import org.apache.commons.mail.SimpleEmail;

public class EmailUtils {
	public static void sendEmail(String toEmail, String msg) throws Exception{
		SimpleEmail email = new SimpleEmail();
		email.setHostName("smtp.163.com");

        email.setAuthentication("wj132990@163.com", "Apptree2009");
        email.setSSLOnConnect(true);
        email.setFrom("413246753@163.com", "局域网聊天系统邮件");
        email.setSubject("用户忘记密码邮件");
        email.setCharset("UTF-8");
        email.setMsg(msg);
        email.addTo(toEmail);
        email.send();
	}

    public static void toEmail(String toEmail, String msg) throws Exception{
        SimpleEmail email = new SimpleEmail();
        email.setHostName("");//邮件服务器

        email.setAuthentication("", "");//邮件登录用户名及密码
        email.setSSLOnConnect(true);
        email.setFrom("", "");//发送方邮箱、发送方名称
        email.setSubject("用户忘记密码邮件");//主题名称
        email.setCharset("UTF-8");//设置字符集编码
        email.setMsg(msg);//发送内容
        email.addTo(toEmail);//接收方邮箱
        email.send();//发送方法
    }
}