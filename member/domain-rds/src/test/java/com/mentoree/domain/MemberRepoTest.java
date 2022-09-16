package com.mentoree.domain;

import com.mentoree.common.domain.Category;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.MemberInterest;
import com.mentoree.member.domain.entity.MemberOAuth;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberInterestRepository;
import com.mentoree.member.domain.repository.MemberOAuthRepository;
import com.mentoree.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MemberRepoTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberInterestRepository memberInterestRepository;
    @Autowired
    private MemberOAuthRepository memberOAuthRepository;

    @Test
    @DisplayName("멤버_ID_검색")
    void 멤버_ID_검색() {
        //given
        Member member = Member.builder()
                .email("test@email.com")
                .authId("googleOAuth")
                .role(UserRole.USER)
                .memberName("testName")
                .nickname("testNick")
                .link("link")
                .build();

        Member saved = memberRepository.save(member);

        //when
        Optional<Member> findEntity = memberRepository.findById(saved.getId());
        //then
        assertThat(findEntity).isNotEmpty();
        assertThat(findEntity.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("이메일로_회원_검색")
    void 이메일로_회원_검색() {
        //given
        dataPreparation();
        //when
        Optional<Member> findResult = memberRepository.findMemberByEmail("test@email.com");
        //then
        assertThat(findResult).isNotEmpty();
        assertThat(findResult.get().getInterest().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("OAuth_가입정보_확인")
    void OAuth_가입정보_확인() {
        //given
        dataPreparation();
        //when
        Optional<MemberOAuth> findResult = memberOAuthRepository.findMemberOauthByAuthId("uniqueOAuthId");
        //then
        assertThat(findResult).isNotEmpty();
        assertThat(findResult.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("회원_관심분야_리스트_검색")
    void 회원_관심분야_리스트_검색() {
        //given
        dataPreparation();
        Member member = memberRepository.findMemberByEmail("test@email.com").get();
        //when
        List<MemberInterest> findResults = memberInterestRepository.findAllByMemberId(member.getId());
        //then
        assertThat(findResults.size()).isGreaterThan(0);
        assertThat(findResults.get(0).getCategory()).isEqualTo(Category.IT);
    }

    private void dataPreparation() {
        Member member = Member.builder()
                .email("test@email.com")
                .authId("googleOAuth")
                .role(UserRole.USER)
                .memberName("testName")
                .nickname("testNick")
                .link("link")
                .build();
        memberRepository.save(member);
        MemberInterest interest = MemberInterest.builder()
                .member(member)
                .category(Category.IT)
                .build();
        member.addInterest(Arrays.asList(interest));
        memberInterestRepository.save(interest);
        MemberOAuth oAuth = MemberOAuth.builder()
                .authId("uniqueOAuthId")
                .email("test@email.com")
                .provider("google")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .expiration(123L)
                .build();
        memberOAuthRepository.save(oAuth);
    }

}
