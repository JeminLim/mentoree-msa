package com.mentoree.member.integration;

import com.mentoree.common.domain.Category;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.MemberInterest;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberInterestRepository;
import com.mentoree.member.domain.repository.MemberRepository;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataPreparation {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberInterestRepository memberInterestRepository;

//    public DataPreparation(MemberRepository memberRepository, MemberInterestRepository memberInterestRepository) {
//        this.memberInterestRepository = memberInterestRepository;
//        this.memberRepository = memberRepository;
//    }
    private final Map<String, Object> data = new HashMap<>();
    public Map<String, Object> getData() {
        init();
        return data;
    }
    private void init() {

        Member memberA = Member.builder()
                .memberName("testerA")
                .email("testA@email.com")
                .nickname("testNickA")
                .role(UserRole.USER)
                .authId("google")
                .build();
        Member memberB = Member.builder()
                .memberName("testerB")
                .email("testB@email.com")
                .nickname("testNickB")
                .role(UserRole.USER)
                .authId("google")
                .build();

        Member savedMemberA = memberRepository.save(memberA);
        Member savedMemberB = memberRepository.save(memberB);

        MemberInterest interestA = MemberInterest.builder().member(savedMemberA).category(Category.IT).build();
        MemberInterest interestB = MemberInterest.builder().member(savedMemberA).category(Category.ART).build();

        List<MemberInterest> interestList = new ArrayList<>();
        interestList.add(interestA);
        interestList.add(interestB);
        memberInterestRepository.saveAll(interestList);

        memberA.addInterest(interestList);
        data.put("memberA", savedMemberA);
        data.put("memberB", savedMemberB);
        data.put("memberAInterest", interestList);

    }


}
