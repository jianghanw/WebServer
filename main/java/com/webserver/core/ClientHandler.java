package com.webserver.core;

import com.webserver.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

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
                File file = new File(
                        ClientHandler.class.getClassLoader().getResource(
                                "./static/myweb/index.html"
                        ).toURI()
                );
                String line = "HTTP/1.1 200 OK";
                line = "Content-Type: text/html";
                println(line);
                line = "Content-Length: "+file.length();
                println(line);
                println("");
                OutputStream out = socket.getOutputStream();
                //send response content
                //define cache 10k, speed up reading data
                byte [] buf = new byte[10*1024];
                int len;
                FileInputStream fis = new FileInputStream(file);
                while((len=fis.read(buf))!=-1)
                {
                    out.write(buf,0,len);
                }
                System.out.println("Response has been sent successfully!");
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }finally{
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    private void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13);
        out.write(10);
    }
}
