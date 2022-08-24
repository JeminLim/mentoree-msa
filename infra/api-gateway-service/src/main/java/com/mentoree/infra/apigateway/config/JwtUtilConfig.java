package com.mentoree.infra.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@ComponentScan(basePackages = {"com.mentoree.common.jwt"})
@Configuration
public class JwtUtilConfig {

}
