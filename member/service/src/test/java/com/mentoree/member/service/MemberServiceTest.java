package com.mentoree.member.service;

import com.mentoree.common.domain.Category;
import com.mentoree.common.dto.messagequeue.UpdateMember;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.MemberInterest;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberInterestRepository;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.dto.MemberInfo;
import com.mentoree.member.messagequeue.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private MemberInterestRepository memberInterestRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원_정보_열람_테스트")
    void 회원_정보_열람_테스트() {
        //given
        Member member = Member.builder()
                .memberName("testMember")
                .nickname("testNick")
                .email("test@email.com")
                .role(UserRole.USER)
                .authId("google")
                .link("test member description")
                .build();
        when(memberRepository.findMemberById(any())).thenReturn(Optional.of(member));
        //when
        MemberInfo memberProfile = memberService.getMemberProfile(1L);
        //then
        assertThat(memberProfile.getEmail()).isEqualTo(member.getEmail());
        assertThat(memberProfile.getLink()).isEqualTo(member.getLink());
        assertThat(memberProfile.getMemberName()).isEqualTo(member.getMemberName());
        assertThat(memberProfile.getNickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("회원_정보_업데이트_테스트")
    void 회원_정보_업데이트_테스트() {
        //given
        Member member = Member.builder()
                .memberName("testMember")
                .nickname("testNick")
                .email("test@email.com")
                .role(UserRole.USER)
                .authId("google")
                .link("test member description")
                .build();

        MemberInfo input = MemberInfo.builder()
                .memberName(member.getMemberName())
                .nickname("changedNick")
                .link("new Link")
                .interests(new ArrayList<>())
                .build();

        when(kafkaProducer.send(any(), any())).thenReturn(
                UpdateMember.builder().memberId(member.getId()).nickname(member.getNickname()).build());
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.of(member));
        when(memberInterestRepository.saveAll(any())).thenReturn(
                Arrays.asList(MemberInterest.builder().member(member).category(Category.IT).build())
        );
        when(memberRepository.save(any())).thenReturn(member);
        //when
        MemberInfo result = memberService.updateMemberProfile(input);
        //then
        assertThat(member.getNickname()).isEqualTo(result.getNickname());
        assertThat(member.getLink()).isEqualTo(result.getLink());
    }



}
