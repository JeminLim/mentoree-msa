//package com.mentoree.member.auth.config;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mentoree.jwt.util.TokenMember;
//import com.mentoree.member.auth.dto.LoginMemberInfo;
//import com.mentoree.member.client.ParticipatedProgramClient;
//import com.mentoree.member.domain.entity.Member;
//import com.mentoree.member.domain.entity.UserRole;
//import com.mentoree.member.domain.repository.MemberRepository;
//import com.mentoree.member.redis.domain.entity.RefreshToken;
//import com.mentoree.member.redis.domain.service.TokenService;
//import com.mentoree.jwt.util.JwtUtils;
//import com.mentoree.encypt.utils.AESUtils;
//import com.mentoree.encypt.utils.EncryptUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.web.DefaultRedirectStrategy;
//import org.springframework.security.web.RedirectStrategy;
//import org.springframework.security.web.server.DefaultServerRedirectStrategy;
//import org.springframework.security.web.server.ServerRedirectStrategy;
//import org.springframework.security.web.server.WebFilterExchange;
//import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
//import org.springframework.security.web.server.savedrequest.ServerRequestCache;
//import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
////@Component
////public class CustomAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
//public class CustomAuthenticationSuccessHandler {
//
//    private final JwtUtils jwtUtils;
//    private final TokenService tokenService;
//    private final EncryptUtils encryptUtils = new AESUtils();
//    private final ObjectMapper objectMapper;
//
//    private final MemberRepository memberRepository;
//    private final ParticipatedProgramClient participatedProgramClient;
//
//    private final int validationTime = 60 * 60 * 2;
//    private final int refreshValidTime = 60 * 60 * 24 * 14;
//
////    @Override
//    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
//
//        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
//        /** Member Info */
//        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
//        String email = principal.getName();
//        long id = Long.parseLong(principal.getAttributes().get("ID").toString());
//        String role = principal.getAttributes().get("ROLE").toString();
//
//        /** Refresh Token */
//        String refreshId = generateEncryptedId();
//        generateRefreshTokenAndSave(email, refreshId);
//
//        /** Send token */
//        sendCookie(response, "access", generateAccessToken(id, email, role), validationTime);
//        sendCookie(response, "refresh", refreshId, refreshValidTime);
//
//        /** Send Member Info to front */
//        Optional<Member> findMember = memberRepository.findById(id);
//        if(findMember.isPresent()) {
//            Member member = findMember.get();
//            LoginMemberInfo loginMember = LoginMemberInfo.builder()
//                    .id(id)
//                    .memberName(member.getMemberName())
//                    .link(member.getLink())
//                    .interests(member.getInterest().stream().map(memberInterest
//                            -> memberInterest.getCategory().getKey()).collect(Collectors.toList()))
//                    .nickname(member.getNickname())
//                    .participatedPrograms(participatedProgramClient.getParticipatedPrograms(id))
//                    .email(member.getEmail())
//                    .build();
//
//            try {
//                String data = objectMapper.writeValueAsString(loginMember);
//                byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
//                DataBuffer buffer = response.bufferFactory().wrap(bytes);
//                response.writeWith(Mono.just(buffer));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return Mono.empty();
//    }
//
//    private void sendCookie(ServerHttpResponse response, String cookieName, String cookieValue, int validation) {
//        response.addCookie(createAccessCookie(cookieName, cookieValue, validation));
//    }
//
//    private String generateAccessToken(Long id, String email, String role) {
//        TokenMember tokenMember = new TokenMember(id, email, UserRole.valueOf(role).getKey());
//        return jwtUtils.generateToken(tokenMember);
//    }
//
//    private void generateRefreshTokenAndSave(String email, String encryptedId) {
//        RefreshToken refreshToken = RefreshToken.builder()
//                .email(email)
//                .identifier(encryptedId)
//                .build();
//        tokenService.save(refreshToken);
//    }
//
//    private String generateEncryptedId() {
//        return encryptUtils.encrypt(UUID.randomUUID().toString());
//    }
//
//    private ResponseCookie createAccessCookie(String cookieName, String value, int validation) {
//        return ResponseCookie.from(cookieName, value)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(validation)
//                .build();
//    }
//
//}
