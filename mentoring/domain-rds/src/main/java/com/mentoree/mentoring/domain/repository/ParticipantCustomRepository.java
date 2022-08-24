package com.mentoree.mentoring.domain.repository;

import com.mentoree.common.interenal.ParticipatedProgram;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.dto.ApplyRequestDto;

import java.util.List;

public interface ParticipantCustomRepository {
    List<ApplyRequestDto> findAllApplicantByProgramId(Long programId);
    int countParticipationByMemberId(Long memberId);
    Participant findApplicantByMemberIdAndProgramId(Long programId, Long memberId);
    List<ParticipatedProgram> findAllProgramByMemberId(Long memberId);

    List<Participant> findAllParticipantByProgramId(Long programId);
    List<Participant> findAllParticipantByMissionId(Long missionId);
    List<Participant> findAllParticipantByBoardId(Long boardId);
    List<Participant> findAllParticipantByMemberId(Long memberId);
}
