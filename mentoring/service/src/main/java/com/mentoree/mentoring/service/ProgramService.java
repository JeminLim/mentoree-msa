package com.mentoree.mentoring.service;

import com.mentoree.common.domain.Category;
import com.mentoree.common.interenal.ParticipatedProgram;
import com.mentoree.mentoring.dto.ParticipatedProgramDto;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.mentoree.mentoring.dto.ProgramCreateDto;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ParticipantRepository participantRepository;

    private final int ALL_PROGRAM_PAGE_SIZE = 8;
    private final int RECOMMEND_PROGRAM_PAGE_SIZE = 8;

    @Transactional
    public ParticipatedProgramDto createProgram(ProgramCreateDto createForm) {
        //프로그램 저장
        Program saveProgram = programRepository.save(createForm.toProgramEntity());
        //참가자 저장
        participantRepository.save(createForm.toParticipantEntity(saveProgram));
        return ParticipatedProgramDto.builder()
                .id(saveProgram.getId())
                .title(saveProgram.getTitle())
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String,Object> getProgramList(Integer page, @Nullable Long memberId) {
        List<Long> participatedProgramList = new ArrayList<>();
        if(memberId != null)
            participatedProgramList = participantRepository.findProgramIdByMemberId(memberId);

        Slice<ProgramInfoDto> list = programRepository.findAllProgram(PageRequest.of(page, ALL_PROGRAM_PAGE_SIZE), participatedProgramList);

        Map<String, Object> result = new HashMap<>();
        result.put("programList", list.getContent());
        result.put("hasNext", list.hasNext());
        return result;
    }

    @Transactional(readOnly = true)
    public Slice<ProgramInfoDto> getRecommendProgramList(Integer page, Long memberId, List<String> interests) {
        log.info("getRecommendProgramList ... " );
        List<Long> participatedProgramList = participantRepository.findProgramIdByMemberId(memberId);
        List<Category> interestList = interests.stream().map(Category::valueOf).collect(Collectors.toList());
        return programRepository.findRecommendProgram(PageRequest.of(page, RECOMMEND_PROGRAM_PAGE_SIZE),
                        participatedProgramList, interestList);
    }

    @Transactional(readOnly = true)
    public ProgramInfoDto getProgramInfo(Long programId) {
        ProgramInfoDto programInfoDto = programRepository.findProgramInfoById(programId).orElseThrow(NoSuchFieldError::new);
        return programInfoDto;
    }

    @Transactional
    public void applyProgram(ApplyRequestDto applyRequest) {

        Long countResult = participantRepository
                .countParticipantByMemberIdAndProgramId(applyRequest.getMemberId(), applyRequest.getProgramId());

        if(countResult > 0) {
            throw new RuntimeException("이미 신청 또는 참가 중입니다.");
        }
        Program targetProgram = programRepository.findById(applyRequest.getProgramId())
                .orElseThrow(NoSuchElementException::new);
        participantRepository.save(applyRequest.toEntity(targetProgram));
    }

    @Transactional(readOnly = true)
    public List<ApplyRequestDto> getApplicants(Long programId) {
        return participantRepository.findAllApplicantByProgramId(programId);
    }

    @Transactional(readOnly = true)
    public boolean isHost(Long programId, Long memberId) {
        return participantRepository.isHost(programId, memberId) > 0;
    }

    @Transactional
    public void approval(Long programId, Long memberId) {
        log.info("programId = " + programId + " and memberId = " + memberId);
        Participant participant = participantRepository.findApplicantByMemberIdAndProgramId(programId, memberId);
        participant.approve();
    }

    @Transactional
    public void reject(Long programId, Long memberId) {
        Participant participant = participantRepository.findApplicantByMemberIdAndProgramId(programId, memberId);
        participantRepository.delete(participant);
    }

    @Transactional(readOnly = true)
    public Long countCurrentMember(Long programId) {
        return participantRepository.countCurrentMember(programId);
    }
    

    @Transactional(readOnly = true)
    public List<ParticipatedProgram> getParticipatedPrograms(Long memberId) {
        return participantRepository.findAllProgramByMemberId(memberId);
    }

}
