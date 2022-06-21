package com.mentoree.member.redis.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 2592000L)
public class RefreshToken {

    @Id
    private String email;
    private String identifier;

    @Builder
    public RefreshToken(String email, String identifier) {
        this.email = email;
        this.identifier = identifier;
    }


}
