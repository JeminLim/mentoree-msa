package com.mentoree.member.auth.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.encrpyt.utils.AESUtils;
import com.mentoree.common.jwt.exception.InvalidTokenException;
import com.mentoree.common.jwt.util.TokenMember;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.redis.domain.entity.BlackListToken;
import com.mentoree.member.redis.domain.entity.RefreshToken;
import com.mentoree.member.redis.domain.service.TokenService;
import com.mentoree.common.jwt.util.JwtUtils;
import com.mentoree.common.encrpyt.utils.EncryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    private EncryptUtils encryptUtils = new AESUtils();

    private final String ACCESS_COOKIE = "access";
    private final String REFRESH_ID = "refresh";

    @Value("${whois.api.key}")
    private String whoisKey;
    @Value("${whois.api.uri}")
    private String whoisUri;


    @GetMapping("/auth/reissue")
    public ResponseEntity reissueAccessToken(HttpServletRequest request) throws IllegalAccessException, ParseException, JsonProcessingException {

        Map<String, String> cookies = getCookies(request);
        String accessToken = cookies.get("accessToken");
        String refreshId = cookies.get("refreshId");

        if(accessToken.isEmpty() || refreshId.isEmpty()) {
            throw new IllegalAccessException("비정상 요청입니다.");
        }

        if (!jwtUtils.validCheck(accessToken)) {
            throw new InvalidTokenException("U002", "비정상 토큰 사용입니다.");
        }

        TokenMember member = jwtUtils.decode(accessToken);
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(NoSuchElementException::new);


        // 해외 IP 접근 여부 확인
        if (isOtherCountryAccess(request)) {
            BlackListToken blackListToken = BlackListToken.builder().authId(findMember.getAuthId()).blockedAccessToken(accessToken).build();
            setBlackList(blackListToken);
            throw new IllegalAccessException("해외 IP 접근으로 차단합니다.");
        }


        String decryptId = encryptUtils.decrypt(refreshId);
        RefreshToken refreshToken = tokenService.findRefreshToken(member.getEmail()).orElseThrow(() -> {
            throw new RuntimeException(member.getEmail() + " user not founded");
        });
        String fromTokenId = encryptUtils.decrypt(refreshToken.getIdentifier());

        // id 일치 검증
        if (!decryptId.equals(fromTokenId)) {
            BlackListToken blackListToken = BlackListToken.builder().authId(findMember.getAuthId()).blockedAccessToken(accessToken).build();
            setBlackList(blackListToken);
            throw new IllegalAccessException("탈취로 의심되는 토큰입니다.");
        }

        String newToken = jwtUtils.generateToken(member);
        return ResponseEntity.ok().body(newToken);
    }

    @PostMapping("/auth/blacklist")
    public ResponseEntity doBlackList(HttpServletRequest request) throws IllegalAccessException {

        Map<String, String> cookies = getCookies(request);
        String accessToken = cookies.get("accessToken");

        if(accessToken.isEmpty()) {
            throw new IllegalAccessException("비정상 요청입니다.");
        }

        TokenMember member = jwtUtils.decode(accessToken);
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(NoSuchElementException::new);

        BlackListToken blackListToken = BlackListToken.builder()
                .blockedAccessToken(accessToken)
                .authId(findMember.getAuthId())
                .build();
        setBlackList(blackListToken);

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);

    }

    @GetMapping("/auth/blacklist/check")
    public ResponseEntity isBlacklist(@RequestParam("token") String token) {
        return ResponseEntity.ok().body(tokenService.isBlacklist(token));
    }

    private Map<String, String> getCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        String accessToken = "";
        String refreshId = "";

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(ACCESS_COOKIE))
                accessToken = cookie.getValue();

            if(cookie.getName().equals(REFRESH_ID))
                refreshId = cookie.getValue();
        }

        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshId", refreshId);
        return result;
    }

    private void setBlackList(BlackListToken blackListToken) {
        tokenService.blackList(blackListToken);
    }

    private boolean isOtherCountryAccess(HttpServletRequest request) throws ParseException, JsonProcessingException {

        String ipAddress = getClientIp(request);

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("serviceKey", whoisKey);
        params.add("query", ipAddress);
        params.add("answer", "JSON");

        HttpEntity<MultiValueMap<String, String>> apiRequest = new HttpEntity<>(params);

        ResponseEntity<String> result = restTemplate.exchange(whoisUri, HttpMethod.GET, apiRequest, String.class);
        JSONParser parser = new JSONParser();
        JSONObject responseResult = (JSONObject) parser.parse(result.getBody());
        HashMap<String, String> parsingResult = objectMapper.readValue(responseResult.toJSONString(), HashMap.class);
        String countryCode = parsingResult.get("countryCode");
        return !countryCode.equals("KR");
    }

    private String getClientIp(HttpServletRequest request) {

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            //Proxy 서버인 경우
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            //Weblogic 서버인 경우
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;


    }

}
