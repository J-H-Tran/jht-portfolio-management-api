package com.pgim.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = "com.pgim.portfolio")
@EnableSpringDataWebSupport
public class MainApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApiApplication.class, args);
    }
}