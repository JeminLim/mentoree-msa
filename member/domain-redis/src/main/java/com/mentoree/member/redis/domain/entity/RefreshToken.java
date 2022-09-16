package com.mentoree.member.redis.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@RedisHash(value = "refreshToken", timeToLive = 2592000L)
public class RefreshToken {

    @Id
    private String authId;

    private final String identifier;

    @Builder
    public RefreshToken(String authId, String identifier) {
        this.authId = authId;
        this.identifier = identifier;
    }


}
