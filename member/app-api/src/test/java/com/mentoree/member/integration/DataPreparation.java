package com.mentoree.member.integration;

import com.mentoree.common.domain.Category;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.MemberInterest;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataPreparation {

    @Autowired
    private MemberRepository memberRepository;

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
                .oAuth2Id("google")
                .build();
        memberB = Member.builder()
                .memberName("testerB")
                .email("testB@email.com")
                .nickname("testNickB")
                .role(UserRole.USER)
                .oAuth2Id("google")
                .build();

        memberA.updateInterest(Arrays.asList("IT", "ART"));

        data.put("memberA", memberA);
        data.put("memberB", memberB);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
    }


}
