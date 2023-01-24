package com.kangming.diary_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DiaryBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiaryBackendApplication.class, args);
    }
}


