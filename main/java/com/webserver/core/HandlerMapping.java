package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HandlerMapping {
    private static Map<String,MethodMapping> map = new HashMap();
    static{
        initMapping();
    }
    private static void initMapping(){
        try {
            /**
             * DispatcherServlet.class.getClassLoader().getResource(".").toURI()
             *   located the dir which is target/classes
             * DispatcherServlet.class.getResource(".").toURI()
             * located the dir which is target/classes/com/webserver/core
             */
            File dir = new File(
                    HandlerMapping.class.getClassLoader().getResource("./com/webserver/controller").toURI()
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
                            String path = rm.value();
                            Object o = cls.newInstance();
                            MethodMapping methodMapping = new MethodMapping(o, method);
                            map.put(path,methodMapping);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static MethodMapping getMethod(String path){
        return map.get(path);
    }
    public static class MethodMapping {
        private Object controller; //业务类对象
        private Method method; //业务方法

        public MethodMapping(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }
}
