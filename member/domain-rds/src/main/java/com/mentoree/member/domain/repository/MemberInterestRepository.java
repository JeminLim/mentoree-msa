package com.mentoree.member.domain.repository;


import com.mentoree.member.domain.entity.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {

    List<MemberInterest> findAllByMemberId(Long memberId);

}
