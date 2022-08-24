package com.mentoree.infra.apigateway.config;

import com.mentoree.common.jwt.util.JwtUtils;
import com.mentoree.infra.apigateway.filters.JwtFilter;
import com.mentoree.infra.apigateway.filters.JwtTokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final String[] EXCLUDE_AUTHORITY_CHECK = {
            "/mentoring-service/api/programs/list/**",
            "/mentoring-service/api/programs/{programId:\\d+}",
            "/member-auth-service/auth/**"
    };

    @Bean
    public SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity http){
        http.httpBasic().disable()
            .formLogin().disable()
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // stateless 방식을 위한 설정
            .authorizeExchange()
            .pathMatchers(EXCLUDE_AUTHORITY_CHECK).permitAll()
            .anyExchange().authenticated()
            .and()
            .cors().and()
            .csrf().disable()
            .addFilterAt(jwtTokenAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC);

        return http.build();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtils);
    }

    @Bean
    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
        JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter =
                new JwtTokenAuthenticationFilter(jwtUtils);

        jwtTokenAuthenticationFilter.setExcludePath(EXCLUDE_AUTHORITY_CHECK);
        return jwtTokenAuthenticationFilter;
    }


}
