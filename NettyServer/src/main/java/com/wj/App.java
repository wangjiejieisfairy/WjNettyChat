package com.wj;

import com.wj.controller.NettyServer;
import java.util.*;
public class App{
    public static void main(String[] args) {

        int port = 8989;
        new NettyServer().serverStart(port);
    }
}
