package com.hyh.mallchat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.hyh.mallchat"})
@MapperScan("com.hyh.mallchat.common.**.mapper")
@ServletComponentScan
public class MallchatCustomApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallchatCustomApplication.class,args);
    }

}