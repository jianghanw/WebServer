package com.webserver.http;

import static com.webserver.http.HttpContext.CR;
import static com.webserver.http.HttpContext.LF;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    private String method;
    private String uri;
    private String protocol;
    private Map<String,String> headers = new HashMap<>();
    private Socket socket;

    public HttpServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        parseRequestLine();
        parseRequestHeaders();

    }
    private void parseRequestLine() throws IOException {
        //parse request line
        String line = readLine();
        System.out.println("Request line: "+line);
        String[] arr = line.split("\\s");
        method = arr[0];
        uri = arr[1];
        protocol = arr[2];
        System.out.println("Request method: "+method);
        System.out.println("Abstract path: "+uri);
        System.out.println("Protocol version: "+protocol);
    }
    private void parseRequestHeaders() throws IOException {
        while(true){
            String line = readLine();
            if(line.isEmpty())
            {
                break;
            }
            String[] data = line.split(":\\s");
            headers.put(data[0],data[1]);
            //System.out.println("Request header: "+ line);
        }
        System.out.println("Headers array: "+headers);
    }
    private void parseContent(){

    }
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        StringBuilder builder = new StringBuilder();
        int d;
        char pre = ' ',cur = ' ';
        while((d=in.read())!=-1){
            cur = (char)d;
            //CR=13,LF=10 (回车+换行)
            if(pre==CR&cur==LF)
            {
                break;
            }
            builder.append(cur);
            pre = cur;
        }
        String line = builder.toString().trim();
        return line;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeaders(String name){
        return headers.get(name);
    }
}
