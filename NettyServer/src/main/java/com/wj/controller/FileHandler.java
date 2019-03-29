package com.wj.controller;
import com.wj.service.DispatchService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileHandler implements Runnable {
    private ServerSocket fsocket;
    private ServerSocket tsocket;
    private String f_name,t_name;
    private DispatchService ds;
    private String pathname;
    /**
     *
     * @param fromport 发送方端口
     * @param toport 接收方端口
     * @param f_name 发送方姓名
     * @param t_name 接收方姓名
     * @param pathname 离线文件路径
     */
    public FileHandler(int fromport,int toport,String f_name,String t_name,String pathname){
        ds = DispatchService.getInstance();
        this.f_name = f_name;
        this.t_name = t_name;
        this.pathname = pathname;
        //接收方和发送方都是在线发送
        if(fromport != 0 || toport != 0){
            try {
                if(fromport != 0){
                    this.fsocket = new ServerSocket(fromport);
                }

                //接收方在线
                if(toport != 0){
                    this.tsocket = new ServerSocket(toport);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void run() {
        try {
            //等待服务端连接

            //进行文件的发送，前提是接收方在线
            if(tsocket != null && fsocket != null) {

                Socket fClient = fsocket.accept();
                Socket tClient = tsocket.accept();
                //发送方
                DataInputStream fData = new DataInputStream(fClient.getInputStream());
                //接收方
                DataOutputStream tData = new DataOutputStream(tClient.getOutputStream());

                tData.writeUTF(fData.readUTF());
                tData.writeLong(fData.readLong());

                byte[] bytes = new byte[1024];

                int len;
                while ((len = fData.read(bytes, 0, bytes.length)) != -1) {
                    tData.write(bytes, 0, bytes.length);
                    tData.flush();
                }

                fData.close();
                tData.close();
            } else if(tsocket == null && fsocket != null){
                System.out.println("接收方不在线，发送方在线........");
                //接收方不在线，发送方在线
                Socket fClient = fsocket.accept();
                //发送方
                DataInputStream fData = new DataInputStream(fClient.getInputStream());
                String name = fData.readUTF();
                //服务器存储路径
                String pathName ="E:"+ File.separator+name;
                System.out.println("服务器存储路径为："+pathName);
                //接收方
                //DataOutputStream tData = new DataOutputStream();
                FileOutputStream tData = new FileOutputStream(pathName);
                fData.readUTF();
                fData.readLong();

                /*byte[] bytes = new byte[1024];

                int len;
                while ((len = fData.read(bytes, 0, bytes.length)) != -1) {
                    tData.write(bytes, 0, len);
                    tData.flush();
                }*/
                int len = 0;
                while((len = fData.read()) != -1){
                  //  tData.write(len);
                   // tData.flush();
                }

                fData.close();
                tData.close();

                System.out.println("存储文件离线信息。。。。。。");
                //存储离线文件路径
                ds.hander(t_name,f_name,pathName,1,2);
                System.out.println("存储文件离线信息成功。。。。。。");



            }else if(fsocket == null && tsocket != null){
                //发送方在线，接收方在线收到离线消息
                //接收方不在线，发送方在线
                Socket tClient = tsocket.accept();
                //发送方
                DataInputStream fData = new DataInputStream(new FileInputStream(pathname));

                //接收方
                DataOutputStream tData = new DataOutputStream(tClient.getOutputStream());

                tData.writeUTF(fData.readUTF());
                tData.writeLong(fData.readLong());

                byte[] bytes = new byte[1024];

                int len;
                while ((len = fData.read(bytes, 0, bytes.length)) != -1) {
                    tData.write(bytes, 0, bytes.length);
                    tData.flush();
                }

                fData.close();
                tData.close();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
