package com.mentoree.member.auth.config;


import com.mentoree.member.redis.domain.entity.RefreshToken;
import com.mentoree.member.redis.domain.service.TokenService;
import com.mentoree.jwt.util.JwtUtils;
import com.mentoree.encypt.utils.AESUtils;
import com.mentoree.encypt.utils.EncryptUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final EncryptUtils encryptUtils = new AESUtils();

    private final int validationTime = 60 * 60 * 2;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = getEmailFromAuth(authentication);
        generateRefreshTokenAndSave(email, generateEncryptedId());
        sendCookie(response, "access", generateAccessToken(email), validationTime);
    }

    private String getEmailFromAuth(Authentication authentication) {
        return ((OAuth2User)authentication.getPrincipal()).getName();
    }

    private void sendCookie(HttpServletResponse response, String cookieName, String cookieValue, int validation) {
        response.addCookie(createAccessCookie(cookieName, cookieValue, validation));
    }

    private String generateAccessToken(String email) {
        return jwtUtils.generateAccessToken(email);
    }

    private void generateRefreshTokenAndSave(String email, String encryptedId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .identifier(encryptedId)
                .build();
        tokenService.save(refreshToken);
    }

    private String generateEncryptedId() {
        return encryptUtils.encrypt(UUID.randomUUID().toString());
    }

    private Cookie createAccessCookie(String cookieName, String value, int validation) {
        Cookie accessCookie = new Cookie(cookieName, value);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(validation);
        return accessCookie;
    }


}
