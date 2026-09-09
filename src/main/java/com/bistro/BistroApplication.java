package com.bistro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BistroApplication {

    public static void main(String[] args) {
        SpringApplication.run(BistroApplication.class, args);
    }
}
