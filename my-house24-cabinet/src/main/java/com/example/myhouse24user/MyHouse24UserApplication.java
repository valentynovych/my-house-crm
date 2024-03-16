package com.example.myhouse24user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MyHouse24UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyHouse24UserApplication.class, args);
    }

}