//package com.mentoree.member.auth.config;
//
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
////@Configuration
////@EnableWebSecurity
//public class SecurityConfig {
//
//    private final CustomOAuth2UserService oAuth2UserService;
////    private final CustomAuthenticationSuccessHandler successHandler;
//
////    @Bean
////    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
////        return http
////                .httpBasic().disable()
////                .formLogin().disable()
////                .authorizeExchange()
////                .pathMatchers("/login/**").permitAll()
////                .anyExchange().authenticated()
//////                .and().oauth2Login(login -> login.authenticationSuccessHandler(successHandler))
//////                .logout(logout -> logout.logoutUrl("/"))
//////                .exceptionHandling()
//////                .accessDeniedHandler(((exchange, denied) -> Mono.error(new Exception("Denied"))))
////                .and().build();
////    }
//
//}
