package com.mentoree.member.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeExchange()
                .pathMatchers("/login").permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login(login -> login.authenticationSuccessHandler(successHandler))
                .logout(logout -> logout.logoutUrl("/"))
                .exceptionHandling()
                .accessDeniedHandler(((exchange, denied) -> Mono.error(new Exception("Denied"))))
                .and().build();
    }

    @Bean
    public ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return oAuth2UserService;
    }

}
