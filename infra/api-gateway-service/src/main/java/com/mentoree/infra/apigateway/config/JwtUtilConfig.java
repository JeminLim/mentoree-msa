package com.mentoree.infra.apigateway.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"com.mentoree.common.jwt"})
@Configuration
public class JwtUtilConfig {

}
