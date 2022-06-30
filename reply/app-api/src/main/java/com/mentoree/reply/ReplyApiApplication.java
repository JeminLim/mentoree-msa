package com.mentoree.reply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ReplyApiApplication {


    public void main(String[] args) {
        SpringApplication.run(ReplyApiApplication.class, args);
    }

}
