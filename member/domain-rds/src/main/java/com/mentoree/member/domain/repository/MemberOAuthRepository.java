package com.mentoree.member.domain.repository;

import com.mentoree.member.domain.entity.MemberOAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberOAuthRepository extends JpaRepository<MemberOAuth, Long> {

    Optional<MemberOAuth> findMemberOauthByAuthId(String AuthId);

}
