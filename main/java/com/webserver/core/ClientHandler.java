package com.webserver.core;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;

/**
 * This thread is responsible for doing the http interaction with the specified client
 * and for each interaction, always follow the principle of one question and one answer,
 * it consists of three step:
 * 1. Parsing the request
 * 2. handle the request
 * 3. send the request
 */
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
                //parse request
                HttpServletRequest request = new HttpServletRequest(socket);
                HttpServletResponse response = new HttpServletResponse(socket);
                //handle the request
                DispatcherServlet handler = new DispatcherServlet();
                handler.service(request,response);
                //send the response
                response.response();
                System.out.println("Response has been sent successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

}
