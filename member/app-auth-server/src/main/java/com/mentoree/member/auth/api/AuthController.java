package com.mentoree.member.auth.api;

import com.mentoree.jwt.util.TokenMember;
import com.mentoree.member.redis.domain.entity.RefreshToken;
import com.mentoree.iptrace.client.WhoisClient;
import com.mentoree.member.redis.domain.service.TokenService;
import com.mentoree.jwt.util.JwtUtils;
import com.mentoree.encypt.utils.EncryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.mentoree.iptrace.dto.WhoIsDto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final EncryptUtils encryptUtils;
    private final TokenService tokenService;

    /** whois 외부 API 관련 */
    private final WhoisClient whoisClient; // ip 정보 openAPI

    @GetMapping("/auth/reissue")
    public ResponseEntity reissueAccessToken(ServerHttpRequest request, @RequestBody String id) throws IllegalAccessException {
        final String accessCookieName = "accessCookie";
        String accessToken = Objects.requireNonNull(request.getCookies().getFirst(accessCookieName)).getValue();

        // 해외 IP 접근 여부 확인
        if (isOtherCountryAccess(request)) {
            setBlackList(accessToken);
            throw new IllegalAccessException("해외 IP 접근으로 차단합니다.");
        }

        TokenMember member = jwtUtils.decode(accessToken);

        String decryptId = encryptUtils.decrypt(id);
        RefreshToken refreshToken = tokenService.findRefreshToken(member.getEmail()).orElseThrow(() -> {
            throw new UsernameNotFoundException(member.getEmail() + " user not founded");
        });
        String fromTokenId = encryptUtils.decrypt(refreshToken.getIdentifier());

        // id 일치 검증
        if(!decryptId.equals(fromTokenId)) {
            setBlackList(accessToken);
            throw new IllegalAccessException("탈취로 의심되는 토큰입니다.");
        }

        String newToken = jwtUtils.generateToken(member);
        return ResponseEntity.ok().body(newToken);
    }


    private void setBlackList(String token) {
        tokenService.blackList(token);
    }

    private boolean isOtherCountryAccess(ServerHttpRequest request) {
        String ipAddress = request.getRemoteAddress().getAddress().getHostAddress();
        WhoIs apiResult = null;

        apiResult = whoisClient.getIpCountryCode(null, ipAddress, "json");


        if(!apiResult.getCountryCode().equals("KR")) {
            log.error("해외 IP 에서 접근을 시도했습니다.");
            return true;
        }
        return false;
    }

}
