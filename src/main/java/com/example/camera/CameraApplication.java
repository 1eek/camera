package com.example.camera;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.example.camera.mapper")
public class CameraApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CameraApplication.class, args);

        // 注册MyShutdownListener
        context.registerShutdownHook();

    }

}
