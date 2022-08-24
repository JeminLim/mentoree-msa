package com.mentoree.member.integration;

import com.mentoree.common.domain.Category;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.MemberInterest;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberInterestRepository;
import com.mentoree.member.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataPreparation {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberInterestRepository memberInterestRepository;

    private Member memberA;
    private Member memberB;

    private Map<String, Object> data = new HashMap<>();

    public Map<String, Object> getData() {
        init();
        return data;
    }

    private void init() {

        memberA = Member.builder()
                .memberName("testerA")
                .email("testA@email.com")
                .nickname("testNickA")
                .role(UserRole.USER)
                .authId("google")
                .build();
        memberB = Member.builder()
                .memberName("testerB")
                .email("testB@email.com")
                .nickname("testNickB")
                .role(UserRole.USER)
                .authId("google")
                .build();

        MemberInterest interestA = MemberInterest.builder().member(memberA).category(Category.IT).build();
        MemberInterest interestB = MemberInterest.builder().member(memberA).category(Category.ART).build();

        List<MemberInterest> interestList = new ArrayList<>();
        interestList.add(interestA);
        interestList.add(interestB);
        memberInterestRepository.saveAll(interestList);
        memberA.addInterest(interestList);

        data.put("memberA", memberA);
        data.put("memberB", memberB);
        data.put("memberAInterest", interestList);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
    }


}
