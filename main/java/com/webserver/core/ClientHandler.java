package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run(){
        /**
         * 按照HTTP协议的规定：浏览器除了组件内容外，其余都是字符串
         * 但是只包含英文，数字，符号，这三种有一个特点：就是全是单字节
         * 所以每个字符就是一个字节
         */
        try {
            InputStream in = socket.getInputStream();
            int d;
            while((d=in.read())!=-1){
                System.out.print((char)d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
