package com.mentoree.member.auth.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.interenal.ParticipatedProgram;
import com.mentoree.common.encrpyt.utils.AESUtils;
import com.mentoree.common.encrpyt.utils.EncryptUtils;
import com.mentoree.common.jwt.util.JwtUtils;
import com.mentoree.common.jwt.util.TokenMember;
import com.mentoree.member.auth.dto.LoginMemberInfo;
import com.mentoree.member.client.ParticipatedProgramClient;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.MemberOAuth;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberOAuthRepository;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.redis.domain.entity.BlackListToken;
import com.mentoree.member.redis.domain.entity.RefreshToken;
import com.mentoree.member.redis.domain.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController {


    /** get config info */
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.token-uri}")
    private String googleTokenUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    /** Inject Dependency */
    private final MemberOAuthRepository memberOAuthRepository;
    private final MemberRepository memberRepository;
    private final ParticipatedProgramClient participatedProgramClient;
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    private EncryptUtils encryptUtils = new AESUtils();

    /** Constant */
    private final int validationTime = 60 * 60 * 2;
    private final int refreshValidTime = 60 * 60 * 24 * 14;

    @PostMapping("/auth/login")
    public ResponseEntity OAuthLogin(HttpServletResponse response, @RequestParam("code") String code, @RequestParam("provider") String provider) {

        log.info("login process .... ");
        LoginMemberInfo loginMember = login(response, code, provider);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("loginMember", loginMember);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/auth/logout/{memberId}")
    public ResponseEntity logout(HttpServletResponse response, @PathVariable("memberId") Long memberId) throws IllegalAccessException {

        Optional<Member> findMember = memberRepository.findById(memberId);
        if(findMember.isEmpty())
            throw new IllegalAccessException("비정상적인 접근 입니다.");

        String authId = findMember.get().getAuthId();
        Optional<MemberOAuth> oauthMember = memberOAuthRepository.findMemberOauthByAuthId(authId);
        if(oauthMember.isEmpty())
            throw new IllegalAccessException("비정상적인 접근 입니다.");

        BlackListToken blacklistToken = BlackListToken.builder()
                .authId(authId)
                .blockedAccessToken(oauthMember.get().getAccessToken())
                .build();
        tokenService.blackList(blacklistToken);

        MemberOAuth memberOAuth = oauthMember.get();
        memberOAuth.updateToken("", "", 0L);
        memberOAuthRepository.save(memberOAuth);

        removeCookie(response);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("result", "success");

        return ResponseEntity.ok().body(responseBody);
    }


    private LoginMemberInfo login(HttpServletResponse response, String code, String provider) {
        try {
            switch (provider) {
                case "naver": // naver oauth
                    return getInfoFromNaver(response, code);
                default: // google oauth
                    return getInfoFromGoogle(response, code);
            }
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private LoginMemberInfo getInfoFromGoogle(HttpServletResponse response, String code) throws ParseException, URISyntaxException, IllegalAccessException {
        log.info("login process with google oauth .... ");
        JSONObject tokenResponse = getTokenFromProvider("google", code);
        String idToken = (String) tokenResponse.get("id_token");

        Map<String, String> decoded = jwtUtils.decodeGoogleToken(idToken);
        String sub = decoded.get("oAuthId");
        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");
        Long expiresIn = (Long) tokenResponse.get("expires_in");
        String name = decoded.get("familyName").concat(decoded.get("givenName"));
        String email = decoded.get("email");

        log.info("member info - sub : {}", sub);
        log.info("member info - accessToken : {}", accessToken);
        log.info("member info - refreshToken : {}", refreshToken);
        log.info("member info - expiresIn : {}", expiresIn);
        log.info("member info - name : {}", name);
        log.info("member info - email : {}", email);

        return getLoginMemberInfo(response, accessToken, refreshToken, sub, expiresIn, email, name, "google");
    }

    private LoginMemberInfo getInfoFromNaver(HttpServletResponse response, String code) throws ParseException, URISyntaxException, JsonProcessingException {
        log.info("login process with naver oauth .... ");
        JSONObject tokenResponse = getTokenFromProvider("naver", code);

        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");
        Long expiresIn = Long.parseLong((String)tokenResponse.get("expires_in"));

        JSONObject infoResponse = getInfoFromProvider(accessToken);
        JSONObject profileJsonString = (JSONObject) infoResponse.get("response");

        HashMap<String, String> profileMap = objectMapper.readValue(profileJsonString.toJSONString(), HashMap.class);
        String email = profileMap.get("email");
        String name = profileMap.get("name");
        String id = profileMap.get("id");

        log.info("member info - id : {}", id);
        log.info("member info - accessToken : {}", accessToken);
        log.info("member info - refreshToken : {}", refreshToken);
        log.info("member info - expiresIn : {}", expiresIn);
        log.info("member info - name : {}", name);
        log.info("member info - email : {}", email);

        return getLoginMemberInfo(response, accessToken, refreshToken, id, expiresIn, email, name, "naver");
    }

    private LoginMemberInfo getLoginMemberInfo(HttpServletResponse response,
                                               String accessToken, String refreshToken, String oAuth2Id, Long expiresIn,
                                               String email, String name, String provider) {

        Optional<MemberOAuth> findMember = memberOAuthRepository.findMemberOauthByAuthId(oAuth2Id);
        if(findMember.isEmpty()) { // new member login
            log.info("New login user with {} - {}", provider, email);
            Member save = saveMember(accessToken, refreshToken, oAuth2Id, expiresIn, email, name, provider);
            sendTokenCookie(response, save);
            return createLoginMemberInfo(save, null);
        }
        else { // existing member login
            MemberOAuth memberOAuth = findMember.get();
            memberOAuth.updateToken(accessToken, refreshToken, expiresIn);
            log.info("DB login user - {}", memberOAuth.getEmail());
            // member의 이메일이 유일한 값이 아닐 수 있다. 네이버 망할놈 때문에 email이 다른 oauth 가입 이메일과 중복될 수 있다는 것.
            Member member = memberRepository.findMemberByEmail(email).orElseThrow(NoSuchElementException::new);
            List<ParticipatedProgram> participatedPrograms = participatedProgramClient.getParticipatedPrograms(member.getId());
            sendTokenCookie(response, member);
            return createLoginMemberInfo(member, participatedPrograms);
        }
    }

    private JSONObject getTokenFromProvider(String provider, String code) throws ParseException, URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");

        MultiValueMap<String, String> accessTokenParams = new LinkedMultiValueMap<>();
        String tokenUri = "";
        String redirectUri = "";
        if(provider.equals("google")) {
            if(Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> env.equalsIgnoreCase("local")))
                redirectUri = "http://localhost:8081/login/oauth2/code/google";
            else
                redirectUri = "http://ec2-43-200-50-181.ap-northeast-2.compute.amazonaws.com:8081/login/oauth2/code/google";

            log.info("redirect url = {}", redirectUri);
            accessTokenParams = accessTokenParams("authorization_code", code, googleClientId, googleClientSecret,
                    redirectUri, null);
            tokenUri = googleTokenUri;
        } else if (provider.equals("naver")) {
            accessTokenParams = accessTokenParams("authorization_code", code, naverClientId, naverClientSecret,
                    null, "oauth_state");
            tokenUri = naverTokenUri;
        }

        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, headers);
        RestTemplate restTemplate = getRestTemplate();
        ResponseEntity<String> accessTokenResponse =  restTemplate.exchange(
                new URI(tokenUri),
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );

        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(accessTokenResponse.getBody());
    }

    private JSONObject getInfoFromProvider(String accessToken) throws ParseException{

        MultiValueMap<String, String> tokenHeader = new LinkedMultiValueMap<>();
        tokenHeader.add("Authorization", "Bearer " + accessToken);
        HttpEntity<Map<String, String>> requestHeaders = new HttpEntity<>(tokenHeader);
        RestTemplate restTemplate = getRestTemplate();
        JSONParser parser = new JSONParser();
        ResponseEntity<String> infoResponse
                = restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, requestHeaders, String.class);
        return (JSONObject) parser.parse(infoResponse.getBody());
    }

    private LoginMemberInfo createLoginMemberInfo(Member save, @Nullable List<ParticipatedProgram> programList) {
        return LoginMemberInfo.builder()
                .id(save.getId())
                .email(save.getEmail())
                .nickname(save.getNickname())
                .memberName(save.getMemberName())
                .link(save.getLink())
                .interests(save.getInterest().isEmpty() ? new ArrayList<>() :
                        save.getInterest().stream().map(mi -> mi.getCategory().getKey())
                                .collect(Collectors.toList()))
                .participatedPrograms(programList)
                .build();
    }

    private MultiValueMap<String, String> accessTokenParams(String grantType,
                                                            String code,
                                                            String clientId,
                                                            String clientSecret,
                                                            @Nullable String redirectUri,
                                                            @Nullable String state) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        if(redirectUri!=null) params.add("redirect_uri", redirectUri);
        if(state!=null) params.add("state", state);
        return params;
    }


    private Member saveMember(String accessToken, String refreshToken, String oAuth2Id, Long expiresIn,  String email, String name, String provider) {
        MemberOAuth oauthMember = MemberOAuth.builder()
                .email(email)
                .authId(oAuth2Id)
                .expiration(expiresIn)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .provider(provider)
                .build();
        memberOAuthRepository.save(oauthMember);
        Member member = Member.builder()
                .authId(oAuth2Id)
                .memberName(name)
                .email(email)
                .nickname(provider.charAt(0) + "-" + UUID.randomUUID().toString())
                .role(UserRole.USER)
                .build();
        Member savedMember = memberRepository.save(member);
        return savedMember;
    }

    private void sendTokenCookie(HttpServletResponse response, Member member) {

        TokenMember tokenMember = new TokenMember(member.getId(), member.getEmail(), member.getRole().getKey());
        String accessToken = jwtUtils.generateToken(tokenMember);

        String encryptedId = encryptUtils.encrypt(UUID.randomUUID().toString());
        RefreshToken refreshToken = RefreshToken.builder()
                .authId(member.getAuthId())
                .identifier(encryptedId)
                .build();
        tokenService.save(refreshToken);

        response.addCookie(generateCookie("access", accessToken, validationTime));
        response.addCookie(generateCookie("refresh", encryptedId, refreshValidTime));
    }

    private void removeCookie(HttpServletResponse response) {
        response.addCookie(generateCookie("access", "", 0));
        response.addCookie(generateCookie("refresh", "", 0));
    }

    private Cookie generateCookie(String cookieName, String value, int expiresIn) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(expiresIn);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(5)
                .build();

        factory.setHttpClient(httpClient);
        return new RestTemplate(factory);
    }

}
