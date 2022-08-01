package com.webserver.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HttpContext {
    public static final char CR = 13;
    public static final char LF = 10;
    private static Map<String,String> mapping = new HashMap<>();
    static{
        initMapping();
    }

    private static void initMapping() {
        Properties prop = new Properties();
        /**
         * load方法中加载配置文件
         * 类名.class.getClassLoader().getResource(".")
         * 这里面的"."就是定位target/classes
         * 类名.class.getResourcesAsStream(".")
         * 这里面的"."就是定位当前类所在的目录
         */
        try {
            prop.load(
                    HttpContext.class.getResourceAsStream(
                    "./web.properties"
                    )
            );

            prop.forEach(
                    (k,v)->mapping.put(k.toString(),v.toString())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMimetype(String ext){
        return mapping.get(ext);
    }

    public static void main(String[] args) {
        mapping.forEach(
                (k,v)-> System.out.println("类型为："+k+"的资源对应的mime值是："+v)
        );
    }
}
