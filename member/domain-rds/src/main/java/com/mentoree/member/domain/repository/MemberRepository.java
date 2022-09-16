package com.mentoree.member.domain.repository;

import com.mentoree.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m left join fetch m.interest where m.email = :email")
    Optional<Member> findMemberByEmail(@Param("email") String email);
    Optional<Member> findMemberById(Long id);
}
