package com.mentoree.member.redis.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "blackList", timeToLive = 2592000L)
public class BlackListToken {

    // Spring redis 사용할때 Id는 javax.persistence 가 아니라 springframework.data.annotation 패키지의 ID 사용
    @Id
    private String blockedAccessToken;

    private String authId;

    @Builder
    public BlackListToken(String blockedAccessToken, String authId) {
        this.blockedAccessToken = blockedAccessToken;
        this.authId = authId;
    }

}
