package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Webserver的主类
 * Webserver是一个Web容器，模拟Tomcat的基础功能
 * Web容器主要有两个任务：
 * 1. 管理部署在容器中的所有网络应用（webapp),每个网络应用俗称”网站“
 * 2、 负责与客户端完成TCP链接，并基于HTTP协议进行交互，使得客户端可以通过网络远程调用服务器中的某个资源
 */
public class WebServerApplication {
    private ServerSocket serverSocket;
    public WebServerApplication(){

        try {
            System.out.println("Server is launching...");
            serverSocket = new ServerSocket(8088);
            System.out.println("Server start up completed!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){

        try {
            while(true) {
                System.out.println("Waiting for client connection...");
                Socket socket = serverSocket.accept();
                System.out.println("A client has been connected");
                ClientHandler handler = new ClientHandler(socket);
                Thread t = new Thread(handler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //http://localhost:8088/ localhost 主机ip 8088
    public static void main(String[] args) {
        WebServerApplication server = new WebServerApplication();
        server.start();
    }
}
