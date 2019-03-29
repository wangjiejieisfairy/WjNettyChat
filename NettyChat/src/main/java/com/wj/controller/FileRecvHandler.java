package com.wj.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 接收文件方操作类
 */
public class FileRecvHandler {
    private Socket recvClient;

    /**
     * 建立和服务端的连接
     * @param ip
     * @param port
     */
    public FileRecvHandler(String ip, int port){
        try {
            System.out.println("接收方连接服务器端口："+port);
            recvClient = new Socket(ip,port);
            System.out.println("接收方连接服务器成功");
        } catch (IOException e) {
            System.out.println("接收方连接服务器异常");
            e.printStackTrace();
        }
    }

    public void recv(){
        try {
            DataInputStream dataInputStream = new DataInputStream(recvClient.getInputStream());
            String name = dataInputStream.readUTF();
            long length = dataInputStream.readLong();

            System.out.println("[接收方]接收文件"+name+"大小"+length);

            //拼装存贮路径
            String pathName ="F:"+ File.separator+name;
            System.out.println("接收方存储接收路径");
            FileOutputStream fileOutputStream = new FileOutputStream(pathName);
            //读取文件


            byte[] bytes = new byte[1024];
            int len;
            System.out.println("[接收方]开始接收文件。。。");
            while((len = dataInputStream.read(bytes,0,bytes.length))!=-1){
                fileOutputStream.write(bytes,0,len);
            }
            fileOutputStream.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取默认路径
     * @return
     */
    private String getDefaultPath(){
        String path =  FileRecvHandler.class.getResource("").getPath();
        int index = path.indexOf("target");
        path = path.substring(0,index);
        return path;
    }
}
