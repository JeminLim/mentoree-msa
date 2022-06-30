package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.dto.ApplyRequestDto;

import java.util.List;

public interface ParticipantCustomRepository {
    List<ApplyRequestDto> findAllApplicantByProgramId(Long programId);
    int countParticipationByMemberId(Long memberId);
    Participant findApplicantByMemberIdAndProgramId(Long programId, Long memberId);
}
