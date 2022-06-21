package com.mentoree.member.redis.domain.service;

import com.mentoree.member.redis.domain.entity.BlackListToken;
import com.mentoree.member.redis.domain.entity.RefreshToken;
import com.mentoree.member.redis.domain.repository.BlackListTokenRepository;
import com.mentoree.member.redis.domain.repository.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public void blackList(String token) {
        BlackListToken blackList = BlackListToken.builder().blockedAccessToken(token).build();
        blackListTokenRepository.save(blackList);
    }

}
