package com.mentoree.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MemberApiApplication {

    public void main(String[] args) {
        SpringApplication.run(MemberApiApplication.class, args);
    }


}
