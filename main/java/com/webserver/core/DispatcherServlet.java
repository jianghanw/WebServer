package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.controller.UserController;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.lang.reflect.Method;
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
        UserController controller = new UserController();
        try {
            /**
             * DispatcherServlet.class.getClassLoader().getResource(".").toURI()
             *   located the dir which is target/classes
             * DispatcherServlet.class.getResource(".").toURI()
             * located the dir which is target/classes/com/webserver/core
             */
            File dir = new File(
                    DispatcherServlet.class.getClassLoader().getResource("./com/webserver/controller").toURI()
            );
            File[] subs = dir.listFiles(f->f.getName().endsWith(".class"));
            for(File sub:subs){
                String fileName = sub.getName();
                String className = fileName.substring(0,fileName.indexOf('.'));
                Class cls = Class.forName("com.webserver.controller."+className);
                if(cls.isAnnotationPresent(Controller.class))
                {
                    Method[] methods = cls.getDeclaredMethods();
                    for(Method method:methods){
                        if(method.isAnnotationPresent(RequestMapping.class)){
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            String value = rm.value();
                            if(path.equals(value)){
                                Object o = cls.newInstance();
                                method.invoke(o,request,response);
                                return;
                            }

                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
