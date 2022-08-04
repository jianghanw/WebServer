package com.webserver.http;

import static com.webserver.http.HttpContext.CR;
import static com.webserver.http.HttpContext.LF;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    private String method;
    private String uri;
    private String protocol;
    private String requestURI;
    private String queryString;
    private Map<String,String> params = new HashMap<>();
    private Map<String,String> headers = new HashMap<>();
    private Socket socket;

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        parseRequestLine();
        parseRequestHeaders();
        parseContent();
    }
    private void parseRequestLine() throws IOException,EmptyRequestException {
        //parse request line
        String line = readLine();
        if(line.isEmpty()){
            throw new EmptyRequestException();
        }
        System.out.println("Request line: "+line);
        String[] arr = line.split("\\s");
        method = arr[0];
        uri = arr[1];
        protocol = arr[2];
        //further, parse the uri
        parseUri();
        System.out.println("Request method: "+method);
        System.out.println("Abstract path: "+uri);
        System.out.println("Protocol version: "+protocol);
    }

    private void parseUri() {
        String[] arr = uri.split("\\?");
        requestURI  = arr[0];
        if(arr.length>1){
            queryString = arr[1];
            parseParams(queryString);
        }
        System.out.println("Request part: "+requestURI);
        System.out.println("Parameter part: "+queryString);
        System.out.println("Parameter Map:"+params);
    }

    private void parseParams(String line){
        try {
            line = URLDecoder.decode(line,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String [] arr = line.split("&");
        for(String s: arr)
        {
            String [] param = s.split("=");
            params.put(param[0],param.length>1?param[1]:null);
        }
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
    private void parseContent() throws IOException {
        System.out.println("Start parsing content...");
        if(headers.containsKey("Content-Length")){
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            System.out.println("The length of the request content:"+contentLength);
            byte[] contentData = new byte[contentLength];
            InputStream in = socket.getInputStream();
            in.read(contentData);
            String contentType = headers.get("Content-Type");
            if("application/x-www-form-urlencoded".equals(contentType)){
                String line = new String(contentData, StandardCharsets.ISO_8859_1);
                System.out.println("The request content: "+line);
                parseParams(line);
            }
        }

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

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParams(String name) {
        return params.get(name);
    }
}
