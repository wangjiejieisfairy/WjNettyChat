package com.wj;

import com.wj.controller.NettyClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String ip = "127.0.0.1";
        int port = 8989;
        new NettyClient().clientStart(ip,port);
    }
}
