package com.mentoree.common.jwt.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mentoree.common.jwt.exception.ExpiredTokenException;
import com.mentoree.common.jwt.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtUtils implements InitializingBean {

    private final String ROLE = "role";
    private final String ID = "id";

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
            throw new ExpiredTokenException("U004", "만료된 토큰 입니다.");
        } catch (SignatureVerificationException | AlgorithmMismatchException | InvalidClaimException e) {
            throw new InvalidTokenException("I001", "정상적인 토큰이 아닙니다.");
        }
    }

    public TokenMember decode(String token) {
        jwtVerifier.verify(token);

        DecodedJWT jwt = JWT.decode(token);

        String email = jwt.getSubject();
        Long id = jwt.getClaim(ID).asLong();
        String role = jwt.getClaim(ROLE).asString();

        return new TokenMember(id, email, role);
    }

    public String generateToken(TokenMember member) {
        return JWT.create()
                .withSubject(member.getEmail())
                .withClaim(ROLE, member.getRole())
                .withClaim(ID, member.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_VALID_TIME))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    public Map<String, String> decodeGoogleToken(String token) {

        DecodedJWT googleDecoded = JWT.decode(token);

        String oAuthId = googleDecoded.getSubject();
        String email = googleDecoded.getClaim("email").asString();
        String familyName = googleDecoded.getClaim("family_name").asString();
        String givenName = googleDecoded.getClaim("given_name").asString();

        Map<String, String> result = new HashMap<>();
        result.put("oAuthId", oAuthId);
        result.put("email", email);
        result.put("familyName", familyName);
        result.put("givenName", givenName);

        return result;
    }

}
