package com.mentoree.member.redis.domain.service;

import com.mentoree.member.redis.domain.entity.BlackListToken;
import com.mentoree.member.redis.domain.entity.RefreshToken;
import com.mentoree.member.redis.domain.repository.BlackListTokenRepository;
import com.mentoree.member.redis.domain.repository.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRedisRepository tokenRedisRepository;
    private final BlackListTokenRepository blackListTokenRepository;

    public RefreshToken save(RefreshToken refreshToken) {
        return tokenRedisRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findRefreshToken(String email) {
        return tokenRedisRepository.findByEmail(email);
    }
    public void blackList(BlackListToken blackListToken) {
        log.info("Set to blacklist target token {}", blackListToken);
        log.info("member auth id = {}", blackListToken.getAuthId());
        blackListTokenRepository.save(blackListToken);
    }
    public boolean isBlacklist(String token) {
        Optional<BlackListToken> findBlacklist = blackListTokenRepository.findByBlockedAccessToken(token);
        return !findBlacklist.isEmpty();
    }

}
