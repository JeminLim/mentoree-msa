package com.mentoree.infra.apigateway.filters;

import com.mentoree.jwt.util.JwtUtils;
import com.mentoree.jwt.util.TokenMember;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtUtils jwtUtils;
    private final String ACCESS_TOKEN_COOKIE = "accessCookie";

    public JwtAuthenticationGatewayFilterFactory(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            List<HttpCookie> cookie = request.getCookies().get(ACCESS_TOKEN_COOKIE);
            if(cookie.isEmpty()) {
                return onError(exchange.getResponse(), "No access token", HttpStatus.BAD_REQUEST);
            }

            String accessToken = cookie.get(0).getValue();
            if(!jwtUtils.validCheck(accessToken)) {
                // blacklist 작업 요청
                return onError(exchange.getResponse(), "invalid token", HttpStatus.BAD_REQUEST);
            }

            TokenMember member = jwtUtils.decode(accessToken);
            if(!hasRole(member, config.role)) {
                return onError(exchange.getResponse(), "no authority to access", HttpStatus.UNAUTHORIZED);
            }

            addAuthorizationHeader(request, member);

            return chain.filter(exchange);
        }));
    }

    private void addAuthorizationHeader(ServerHttpRequest request, TokenMember member) {
        request.mutate()
                .header("X-Authorization-User", member.getEmail())
                .header("X-Authorization-Role", member.getRole())
                .build();
    }

    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private boolean hasRole(TokenMember member, String role) {
        return role.equals(member.getRole());
    }

    public static class Config {
        private String role;
    }
}
