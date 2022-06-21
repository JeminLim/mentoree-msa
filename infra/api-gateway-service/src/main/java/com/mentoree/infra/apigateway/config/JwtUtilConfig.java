package com.mentoree.infra.apigateway.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan({"com.mentoree.jwt.util"})
@Configuration
public class JwtUtilConfig {

}
