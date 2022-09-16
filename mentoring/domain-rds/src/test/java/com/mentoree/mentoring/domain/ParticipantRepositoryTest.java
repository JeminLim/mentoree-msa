package com.mentoree.mentoring.domain;

import com.mentoree.common.interenal.ParticipatedProgram;
import com.mentoree.mentoring.common.DataPreparation;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ParticipantRepositoryTest {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private DataPreparation dataPreparation;

    Map<String, Object> entity;

    @BeforeEach
    void init() {
        entity = dataPreparation.getEntity();
    }

    @Test
    @DisplayName("모든_신청자_찾기_테스트_성공")
    void 모든_신청자_찾기_테스트_성공() {
        //given
        Program program = (Program) entity.get("programA");
        //when
        List<ApplyRequestDto> applicants = participantRepository.findAllApplicantByProgramId(program.getId());
        //then
        assertThat(applicants.size()).isGreaterThanOrEqualTo(0);
        assertThat(applicants.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("참가_여부_확인_테스트_성공")
    void 참가_여부_확인_테스트_성공() {
        //given
        // 멤버 ID 1 -> 참가중인 인원
        //when
        int result = participantRepository.countParticipationByMemberId(1L);
        //then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("현재_참가중인_프로그램_테스트_성공")
    void 현재_참가중인_프로그램_테스트_성공() {
        //given
        //멤버 ID 1의 참가중인 프로그램
        Program program = (Program) entity.get("programA");
        //when
        List<Long> result = participantRepository.findProgramIdByMemberId(1L);
        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(program.getId());
    }

    @Test
    @DisplayName("중복_지원_여부_테스트_성공")
    void 중복_지원_여부_테스트_성공() {
        //given
        Program program = (Program) entity.get("programA");
        //when
        boolean isApply = participantRepository.countParticipantByMemberIdAndProgramId(1L, program.getId()) > 0;
        //then
        assertThat(isApply).isTrue();
    }

    @Test
    @DisplayName("지원자_찾기_테스트_성공")
    void 지원자_찾기_테스트_성공() {
        //given
        // 지원자 멤버 ID -> 2
        Program program = (Program) entity.get("programA");
        //when
        Participant result =
                participantRepository.findApplicantByMemberIdAndProgramId(program.getId(), 2L);
        //then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("현재_참가중인_멤버_인원수_테스트")
    void 현재_참가중인_멤버_인원수_테스트() {
        //given
        Program program = (Program) entity.get("programA");
        //when
        Long result = participantRepository.countCurrentMember(program.getId());
        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("프로그램_개최자_여부_확인")
    void 프로그램_개최자_여부_확인() {
        //given
        //프로그램 개최자 멤버 ID -> 1
        Program program = (Program) entity.get("programA");
        //when
        boolean result = participantRepository.isHost(program.getId(), 1L) > 0;
        //then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("참가_프로그램_내부_요청_테스트")
    void 참가_프로그램_내부_요청_테스트() {
        //given
        Program programA = (Program) entity.get("programA");
        Program programB = (Program) entity.get("programB");

        //when
        List<ParticipatedProgram> allPrograms = participantRepository.findAllProgramByMemberId(1L);
        //then
        assertThat(allPrograms.size()).isEqualTo(2);

    }

}
