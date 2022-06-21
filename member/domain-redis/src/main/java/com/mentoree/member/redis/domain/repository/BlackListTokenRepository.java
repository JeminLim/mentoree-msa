package com.mentoree.member.redis.domain.repository;

import com.mentoree.member.redis.domain.entity.BlackListToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlackListTokenRepository extends CrudRepository<BlackListToken, String> {

    Optional<BlackListToken> findByBlockedAccessToken(String token);

}
