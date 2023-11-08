package com.example.camera;

public class CommonKit {
    /**
     * 获取项目webapp目录
     * @return String
     */
    public static String getWebPath() {
        return CommonKit.class.getClassLoader().getResource("").getPath().substring(1) + "lib\\";
    }
}
