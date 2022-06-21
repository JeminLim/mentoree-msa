package com.mentoree.domain;

import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MemberRepoTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member preMember;

    @BeforeEach
    public void data_set_up() {
        preMember = Member.builder()
                .email("preMember@email.com")
                .memberName("preMember")
                .nickname("preMemberNick")
                .oAuth2Id("google")
                .role(UserRole.USER)
                .build();

       memberRepository.save(preMember);
    }

    @Test
    public void 멤버_저장_테스트_성공() {

        //given
        Member member = Member.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testNick")
                .oAuth2Id("google")
                .role(UserRole.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        //when
        Optional<Member> findMember = memberRepository.findById(savedMember.getId());

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember.get().getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void 멤버_변경_테스트_성공() {

        //given
        Member preMember = memberRepository.findById(this.preMember.getId()).get();

        //when
        preMember.updateNickname("changedNickname");
        memberRepository.save(preMember);

        //then
        assertThat(preMember.getNickname()).isEqualTo("changedNickname");
    }

    @Test
    public void 멤버_삭제_테스트_성공() {

        //given
        //when
        memberRepository.delete(preMember);

        //then
        Optional<Member> afterDelete = memberRepository.findById(preMember.getId());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    public void 멤버_검색_BY_이메일() {
        //given
        //when
        Optional<Member> findMember = memberRepository.findByEmail(preMember.getEmail());
        //then
        assertThat(findMember).isNotEmpty();
        assertThat(findMember.get().getId()).isEqualTo(preMember.getId());
    }

}
