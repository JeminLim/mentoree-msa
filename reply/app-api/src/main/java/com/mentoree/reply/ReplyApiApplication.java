package com.mentoree.reply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.mentoree.reply", "com.mentoree.common"})
public class ReplyApiApplication {


    public static void main(String[] args) {
        SpringApplication.run(ReplyApiApplication.class, args);
    }

}
