package com.mentoree.infra.apigateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.jwt.exception.ExpiredTokenException;
import com.mentoree.common.jwt.exception.InvalidTokenException;
import com.mentoree.common.jwt.util.JwtUtils;
import com.mentoree.common.jwt.util.TokenMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    private final String ACCESS_TOKEN_COOKIE = "access";
    private final JwtUtils jwtUtils;

    @Value("${server.url}")
    private String URL;

    public JwtFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter(((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            log.info("request path = {}", request.getURI().getPath());
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            log.info("num of exclude url = {}", config.getExcludeUrl().length);

            for (String whiteList : config.getExcludeUrl()) {
                log.info("white list - {}", whiteList);
                if(antPathMatcher.match(whiteList, request.getURI().getPath())) {
                    log.info("jwt authentication filter not applied");
                    return chain.filter(exchange);
                }
            }

            log.info("jwt authentication filter apply ...");

            List<HttpCookie> cookie = request.getCookies().get(ACCESS_TOKEN_COOKIE);
            if(cookie.isEmpty()) {
                return onError(exchange.getResponse(), "U001", "No access token", HttpStatus.BAD_REQUEST);
            }

            String accessToken = cookie.get(0).getValue();

            /** blacklist 확인 */
            WebClient.create().get().uri(URL + "/member-auth-service/auth/blacklist/check?token=" + accessToken)
                    .retrieve().bodyToMono(Boolean.class).flatMap( response -> {
                        log.info("response value = {}", response);
                        if(!response)
                            return onError(exchange.getResponse(), "U002", "invalid token", HttpStatus.BAD_REQUEST);
                        return Mono.just(true);
            });

            try {
                jwtUtils.validCheck(accessToken);
            } catch (ExpiredTokenException e) {
                return onError(exchange.getResponse(), "U004", "expired token", HttpStatus.BAD_REQUEST);
            } catch (InvalidTokenException e) {
                return onError(exchange.getResponse(), "U002", "invalid token", HttpStatus.BAD_REQUEST);
            }

            TokenMember member = jwtUtils.decode(accessToken);
            log.info("token info - id {" + member.getId() + "}");
            log.info("token info - email {" + member.getEmail() + "}");
            log.info("token info - role {" + member.getRole() + "}");


            if(!hasRole(member, config.getRole())) {
                return onError(exchange.getResponse(), "U003", "no authority to access", HttpStatus.UNAUTHORIZED);
            }

            ServerHttpRequest addHeaderRequest = addAuthorizationHeader(request, member);

            return chain.filter(exchange.mutate().request(addHeaderRequest).build());
        }), 1);
    }

    private ServerHttpRequest addAuthorizationHeader(ServerHttpRequest request, TokenMember member) {
        return request.mutate()
                .header("X-Authorization-User", member.getEmail())
                .header("X-Authorization-Role", member.getRole())
                .header("X-Authorization-Id", member.getId().toString())
                .build();
    }

    private Mono<Void> onError(ServerHttpResponse response, String code, String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        error.put("code", code);

        String errorResponseBody = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            errorResponseBody = objectMapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        response.setStatusCode(status);
        DataBuffer buffer = response.bufferFactory().wrap(errorResponseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private boolean hasRole(TokenMember member, String role) {
        return role.equals(member.getRole());
    }

    public static class Config {
        private String role;
        private String[] excludeUrl;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String[] getExcludeUrl() {
            return excludeUrl;
        }

        public void setExcludeUrl(String... url) {
            this.excludeUrl = url;
        }
    }
}
