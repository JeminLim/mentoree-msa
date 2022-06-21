package com.mentoree.mentoring.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MentoringApiApplication {

    public void main(String[] args) {
        SpringApplication.run(MentoringApiApplication.class, args);
    }


}
