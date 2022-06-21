package com.mentoree.jwt.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils implements InitializingBean {

    private final String ROLE = "role";

    @Value("${spring.jwt.secret-key}")
    private String secretKey;
    @Value("${spring.jwt.accessToken.validation}")
    private int ACCESS_VALID_TIME;

    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.jwtVerifier = JWT.require(algorithm).acceptLeeway(5).build(); // acceptLeeway - 시간 차이의 오차를 인정
    }

    public boolean validCheck(String token) {
        try {
            jwtVerifier.verify(token);
            return true;
        } catch (TokenExpiredException e) {
            log.error("토큰 만료");
            throw e;
        }catch (SignatureVerificationException | AlgorithmMismatchException | InvalidClaimException e) {
            log.error("토큰 이상 문제");
            // blacklist 추가 요망
            throw e;
        }
    }

    public TokenMember decode(String token) {
        jwtVerifier.verify(token);

        DecodedJWT jwt = JWT.decode(token);

        String id = jwt.getSubject();
        String role = jwt.getClaim(ROLE).asString();
        return new TokenMember(id, role);
    }

    public String generateToken(TokenMember member) {
        return JWT.create()
                .withSubject(member.getEmail())
                .withClaim(ROLE, member.getRole())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_VALID_TIME))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

}
