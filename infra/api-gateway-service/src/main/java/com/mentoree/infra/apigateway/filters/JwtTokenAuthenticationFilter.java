package com.mentoree.infra.apigateway.filters;

import com.ctc.wstx.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.jwt.exception.ExpiredTokenException;
import com.mentoree.common.jwt.exception.InvalidTokenException;
import com.mentoree.common.jwt.util.JwtUtils;
import com.mentoree.common.jwt.util.TokenMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private String[] excludePath;
    @Value("${server.url}")
    private String URL;

    public void setExcludePath(String... path) { this.excludePath = path;}

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 예외 경로일 때, 필터 미작동
        if(whiteListCheck(excludePath, exchange))
            return chain.filter(exchange);


        String token = resolveToken(exchange.getRequest());
        // 토큰 유무 검사
        if(!StringUtils.hasText(token))
            return onError(exchange.getResponse(), "U001", "No access token", HttpStatus.BAD_REQUEST);

        /** blacklist 확인 */
        WebClient.create().get().uri(URL + "/member-auth-service/auth/blacklist/check?token=" + token)
        .retrieve().bodyToMono(Boolean.class).flatMap( response -> {
            log.info("response value = {}", response);
            if(!response)
                return onError(exchange.getResponse(), "U002", "invalid token", HttpStatus.BAD_REQUEST);
            return Mono.just(true);
        });

        // 토큰 유효성 검사
        try {
            jwtUtils.validCheck(token);
        } catch (ExpiredTokenException e) {
            return onError(exchange.getResponse(), "U004", "expired token", HttpStatus.BAD_REQUEST);
        } catch (InvalidTokenException e) {
            return onError(exchange.getResponse(), "U002", "invalid token", HttpStatus.BAD_REQUEST);
        }

        TokenMember member = jwtUtils.decode(token);
        ServerHttpRequest addHeaderRequest = addAuthorizationHeader(exchange.getRequest(), member);
        Authentication authentication = generateAuthenticationFromToken(member);

        return chain.filter(exchange.mutate().request(addHeaderRequest).build())
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private String resolveToken(ServerHttpRequest request) {
        List<HttpCookie> accessCookie = request.getCookies().get("access");
        if(accessCookie.isEmpty())
            return null;
        return accessCookie.get(0).getValue();
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

    private Authentication generateAuthenticationFromToken(TokenMember member) {
        List<SimpleGrantedAuthority> authorities
                = Collections.singletonList(new SimpleGrantedAuthority(member.getRole()));
        User principal = new User(member.getEmail(),"", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private boolean whiteListCheck(String[] excludePath, ServerWebExchange exchange) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String path = exchange.getRequest().getURI().getPath();
        return Arrays.stream(excludePath).filter(p -> pathMatcher.match(p, path)).count() > 0;
    }

    private ServerHttpRequest addAuthorizationHeader(ServerHttpRequest request, TokenMember member) {
        return request.mutate()
                .header("X-Authorization-User", member.getEmail())
                .header("X-Authorization-Role", member.getRole())
                .header("X-Authorization-Id", member.getId().toString())
                .build();
    }

}
