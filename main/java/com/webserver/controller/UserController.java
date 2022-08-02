package com.webserver.controller;

import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class UserController {

    private static File USER_DIR = new File("./users");
    static{
        if(!USER_DIR.exists()){
            USER_DIR.mkdirs();
        }
    }

    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("Start processing user registration...");
        String username = request.getParams("username");
        String password = request.getParams("password");
        String nickname = request.getParams("nickname");
        String ageStr = request.getParams("age");
        System.out.println(username+","+password+","+nickname+","+ageStr);

        //use User instance to represent user info, and serialize to the file
        int age = Integer.parseInt(ageStr);
        User user = new User(username,password,nickname,age);
        File userFile = new File(USER_DIR, username + ".obj");
        try(
                FileOutputStream fos = new FileOutputStream((userFile));
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            //serialize the user object to userFile
            oos.writeObject(user);
        }catch(IOException e){

        }
        System.out.println("Finished processing user registration!!");

    }
}
