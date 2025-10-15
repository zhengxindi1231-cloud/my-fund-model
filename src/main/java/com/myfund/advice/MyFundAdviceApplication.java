package com.myfund.advice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口：负责启动基金智能投顾服务。
 */
@SpringBootApplication
public class MyFundAdviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyFundAdviceApplication.class, args);
    }
}
