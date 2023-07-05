package com.anthonyzero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class IdFactoryApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(IdFactoryApplication.class, args);
        new CountDownLatch(1).await();
    }
}
