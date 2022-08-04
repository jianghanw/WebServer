package com.webserver.http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.webserver.http.HttpContext.CR;
import static com.webserver.http.HttpContext.LF;

public class HttpServletResponse {
    private int statusCode = 200;
    private String statusReason = "OK";
    private Socket socket;
    //response header info
    private Map<String,String> headers = new HashMap<>();
    private byte[] contentData;
    private File contentFile;
    private ByteArrayOutputStream baos;
    public HttpServletResponse(Socket socket){
        this.socket = socket;
    }

    /**
     * send response
     * send back the response object following the response format
     */
    public void response() throws IOException {
        preCheck();
        sendStatusLine();
        sendHeaders();
        sendContent();
    }
    private void preCheck(){
        if(baos!=null){
             contentData = baos.toByteArray();//get the byte array from baos
            addHeader("Content-Length",contentData.length+"");
        }
    }
    private void sendStatusLine() throws IOException {
        String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
        println(line);
    }

    private void sendHeaders() throws IOException {
        Set<Map.Entry<String, String>> entrySet = headers.entrySet();
        for(Map.Entry<String, String>e: entrySet){
            String name = e.getKey();
            String value = e.getValue();
            String line = name + ": "+value;
            println(line);
            System.out.println("Sending response header:"+line);
        }
        println("");
    }

    private void sendContent() throws IOException {
        OutputStream out = socket.getOutputStream();
        if(contentData!=null){ //has dynamic data
            out.write(contentData);
        }
        //send response content
        //define cache 10k, speed up reading data
        else if(contentFile!=null) {
            byte[] buf = new byte[10 * 1024];
            int len;
            try (
                    FileInputStream fis = new FileInputStream(contentFile);
            ) {
                while ((len = fis.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    private void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        //回车加换行
        out.write(CR);
        out.write(LF);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getContentFile() {
        return contentFile;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;

        String name = contentFile.getName();
        String ext = name.substring(name.lastIndexOf(".")+1);
        String mime = HttpContext.getMimetype(ext);
        addHeader("Content-Type",mime);
        addHeader("Content-Length",contentFile.length()+"");
    }

    public void addHeader(String name, String value){
        this.headers.put(name,value);
    }

    public OutputStream getOutputStream() {
        //prevent baos object from creating multiple times
        if(baos==null){
            baos = new ByteArrayOutputStream();
        }
        return baos;
    }

    public PrintWriter getWriter(){
        return new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(
                        getOutputStream(),
                        StandardCharsets.UTF_8
                )
            )
        ,true);
    }

    public void setContentType(String mime){
        addHeader("Content-Type",mime);
    }
}
