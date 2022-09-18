package com.mentoree.infra.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Configuration
public class RouteConfig {

    @Bean
    RouterFunction staticResourceLocator() {
        return RouterFunctions.resources("/docs/**", new ClassPathResource("static/docs/"));
    }

}
