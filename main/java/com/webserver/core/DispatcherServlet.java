package com.webserver.core;

import com.webserver.controller.UserController;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;


public class DispatcherServlet {
    private static File staticDir;
    static{
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
    public void service(HttpServletRequest request, HttpServletResponse response){
        String path = request.getRequestURI();
        System.out.println("The abstract path of the request: "+path);
        if("/myweb/reg".equals(path)){
            UserController controller = new UserController();
            controller.reg(request,response);
        }else{
            File file= new File(staticDir,path);
            if(file.isFile())
            {
                response.setContentFile(file);
            }else{
                response.setStatusCode(404);
                response.setStatusReason("Not Found");
                file = new File(staticDir,"root/404.html");
                response.setContentFile(file);
            }
        }


    }
}
