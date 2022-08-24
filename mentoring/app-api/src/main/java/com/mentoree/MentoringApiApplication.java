package com.mentoree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.mentoree.mentoring", "com.mentoree.common"})
public class MentoringApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentoringApiApplication.class, args);
    }

}
