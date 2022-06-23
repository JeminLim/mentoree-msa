package com.mentoree.member.service;

import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.dto.MemberInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

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
                .oAuth2Id("google")
                .link("test member description")
                .build();
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        //when
        MemberInfo memberProfile = memberService.getMemberProfile("test@email.com");
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
                .oAuth2Id("google")
                .link("test member description")
                .build();
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        MemberInfo input = MemberInfo.builder()
                .memberName(member.getMemberName())
                .nickname("changedNick")
                .link("new Link")
                .interests(new ArrayList<>())
                .build();
        //when
        memberService.updateMemberProfile(input);
        //then
        assertThat(member.getNickname()).isEqualTo(input.getNickname());
        assertThat(member.getLink()).isEqualTo(input.getLink());
    }



}
