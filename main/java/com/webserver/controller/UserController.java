package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.core.ClientHandler;
import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Controller
public class UserController {

    private static File USER_DIR = new File("./users");
    private static File staticDir;
    static{
        if(!USER_DIR.exists()){
            USER_DIR.mkdirs();
        }
        try {
            staticDir = new File(
                    ClientHandler.class.getClassLoader().getResource(
                            "./static"
                    ).toURI()
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/myweb/reg")
    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("Start processing user registration...");
        String username = request.getParams("username");
        String password = request.getParams("password");
        String nickname = request.getParams("nickname");
        String ageStr = request.getParams("age");
        System.out.println(username+","+password+","+nickname+","+ageStr);
        if(username==null||password==null||nickname==null||ageStr==null||!ageStr.matches("[0-9]+")){
            File file = new File(staticDir, "/myweb/reg_fail.html");
            response.setContentFile(file);
            return;
        }
        //use User instance to represent user info, and serialize to the file
        int age = Integer.parseInt(ageStr);
        User user = new User(username,password,nickname,age);
        File userFile = new File(USER_DIR, username + ".obj");
        if(userFile.exists()){
            File file = new File(staticDir,"/myweb/have_user.html");
            response.setContentFile(file);
            return;
        }
        try(
                FileOutputStream fos = new FileOutputStream((userFile));
                //为了方便写对象，我们串联了一个序列化（对象输出流）
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            //serialize the user object to userFile
            oos.writeObject(user);
            File file = new File(staticDir, "/myweb/reg_success.html");
            response.setContentFile(file);
        }catch(IOException e){
            
        }
        System.out.println("Finished processing user registration!!");
    }

    @RequestMapping("/myweb/login")
    public void login(HttpServletRequest request, HttpServletResponse response){
        System.out.println("Start processing user login...");
        String username = request.getParams("username");
        String password = request.getParams("password");
        System.out.println(username+","+password);
        if(username==null||password==null){
            File file = new File(staticDir, "/myweb/login_info_error.html");
            response.setContentFile(file);
            return;
        }
        File userFile = new File(USER_DIR, username + ".obj");
        File file = null;
        if(!userFile.exists())
        {
            file = new File(staticDir,"/myweb/login_fail.html");
            response.setContentFile(file);
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(userFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            User user = (User)(ois.readObject());
            if(user.getPassword().equals(password))
            {
                file = new File(staticDir,"/myweb/login_success.html");
            }
            else
            {
                file = new File(staticDir,"/myweb/login_fail.html");
            }
            response.setContentFile(file);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Finished processing user Login!!");
    }

    @RequestMapping("/myweb/showAllUser")
    public void showAllUser(HttpServletRequest request, HttpServletResponse response){
        System.out.println("Start generating dynamic page...");
        ArrayList<User> userList = new ArrayList<>();
        File[] subs = USER_DIR.listFiles(f->f.getName().endsWith(".obj"));
        for(File userFile:subs)
        {
            try {
                FileInputStream fis = new FileInputStream(userFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                userList.add((User)(ois.readObject()));
            } catch (IOException | ClassNotFoundException  e) {
                e.printStackTrace();
            }
        }
        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"en\">");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>Userlist</title>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<h2>User list</h2>");
        pw.println("<ol>");
        for(User user:userList)
        {
            pw.println("<li>"+user.getUsername()+"</li>");
        }
        pw.println("</ol>");
        pw.println("</body>");
        pw.println("</html>");
        //set Content-Type
        response.setContentType("text/html");
        System.out.println("Generating dynamic page completed!");
    }
}
