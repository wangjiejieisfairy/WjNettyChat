package com.wj.controller;

import java.io.*;
import java.net.Socket;

/**
 * 客户端发送文件类
 */
public class FileSendHandler {
    //和服务端连接socket
    private Socket client;
    /**
     * 封装一个方法接收ip和端口
     * @param ip
     * @param port
     */
    public FileSendHandler(String ip,int port){
        try {
            client = new Socket(ip,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文件操作
     * @param file
     */
    public void sendFile(File file){
        try {
            FileInputStream inputStream = new FileInputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
            //文件名
            String name = file.getName();
            //文件大小
            long length = file.length();
            dataOutputStream.writeUTF(name);
            dataOutputStream.writeLong(length);
            dataOutputStream.flush();

            byte[] bytes = new byte[1024];
            int len;
            //读取文件内容并写到网络socket上
            while((len = inputStream.read(bytes,0,bytes.length)) != -1){
                    dataOutputStream.write(bytes,0,len);
                    dataOutputStream.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
