package com.example.myhouse24admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MyHouse24AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyHouse24AdminApplication.class, args);
    }

}
