package com.mentoree.domain;

import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.ParticipatedProgram;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.domain.repository.ParticipatedProgramRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ParticipatedProgramRepoTest {

    @Autowired
    private ParticipatedProgramRepository participatedProgramRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member preMember;
    private ParticipatedProgram participated;

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

        participated = ParticipatedProgram.builder()
                .programTitle("testTitle")
                .programId(1L)
                .member(preMember)
                .build();
        participatedProgramRepository.save(participated);
    }

    @Test
    public void 참가프로그램_생성_테스트() {

        //given
        //when
        Optional<ParticipatedProgram> saved = participatedProgramRepository.findById(participated.getId());
        //then
        assertThat(saved).isNotEmpty();
    }

    @Test
    public void 참가프로그램_삭제_테스트() {

        //given
        //when
        participatedProgramRepository.delete(participated);
        Optional<ParticipatedProgram> result = participatedProgramRepository.findById(participated.getId());
        //then
        assertThat(result).isEmpty();
    }


}
