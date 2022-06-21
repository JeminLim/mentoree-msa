package com.mentoree.member.redis.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@RedisHash(value = "refreshToken")
public class BlackListToken {

    @Id
    private String id;
    private String blockedAccessToken;

    @Builder
    public BlackListToken(String blockedAccessToken) {
        this.blockedAccessToken = blockedAccessToken;
    }

}
