package com.mentoree.repository;

import com.mentoree.entity.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {

    List<MemberInterest> findMemberInterestByMember(Long memberId);
}
