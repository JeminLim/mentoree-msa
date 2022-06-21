package com.mentoree.mentoring.service;

import com.mentoree.common.domain.Category;
import com.mentoree.mentoring.dto.ParticipatedProgramDto;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.entity.ProgramRole;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.mentoree.mentoring.dto.ProgramCreateDto;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import com.mentoree.mentoring.messagequeue.connect.ConnectProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ParticipantRepository participantRepository;
    private final ConnectProducer connectProducer;

    private final int ALL_PROGRAM_PAGE_SIZE = 8;
    private final int RECOMMEND_PROGRAM_PAGE_SIZE = 8;

    @Value("${kafka.topics.programs}")
    private String programKafkaTopic;

    @Value("${kafka.topics.participants}")
    private String participantsKafkaTopic;

    @Transactional
    public ParticipatedProgramDto createProgram(ProgramCreateDto createForm) {
        createForm.setProgramRole(createForm.getMentor() ? ProgramRole.MENTOR.getKey() : ProgramRole.MENTEE.getKey());
        Program toEntity = createForm.toEntity(Category.valueOf(createForm.getCategory()));

        Program save = (Program) connectProducer.send(programKafkaTopic, toEntity);

        Participant participant = Participant.builder()
                .program(save)
                .approval(true)
                .isHost(true)
                .memberId(createForm.getMemberId())
                .role(ProgramRole.valueOf(createForm.getProgramRole()))
                .build();
        connectProducer.send(participantsKafkaTopic, participant);

        return ParticipatedProgramDto.builder()
                .id(save.getId())
                .title(save.getProgramName())
                .build();
    }

    @Transactional
    public List<ProgramInfoDto> getProgramList(Integer page, Long memberId) {
        List<Long> participatedProgramList = participantRepository.findProgramIdByMemberId(memberId);
        return programRepository.findAllProgram(PageRequest.of(page, ALL_PROGRAM_PAGE_SIZE), participatedProgramList).getContent();
    }

    @Transactional
    public List<ProgramInfoDto> getRecommendProgramList(Integer page, Long memberId, List<String> interests) {
        List<Long> participatedProgramList = participantRepository.findProgramIdByMemberId(memberId);
        List<Category> interestList = interests.stream().map(Category::valueOf).collect(Collectors.toList());
        return programRepository
                .findRecommendProgram(PageRequest.of(page, RECOMMEND_PROGRAM_PAGE_SIZE),
                                            participatedProgramList
                                            ,interestList)
                .getContent();
    }

    @Transactional
    public ProgramInfoDto getProgramInfo(Long programId) {
        return programRepository.findProgramInfoById(programId).orElseThrow(NoSuchFieldError::new);
    }

    @Transactional
    public void applyProgram(ApplyRequestDto applyRequest) {

        Long countResult = participantRepository
                .isApplicant(applyRequest.getMemberId(), applyRequest.getProgramId());

        if(countResult > 0) {
            throw new RuntimeException("이미 신청 또는 참가 중입니다.");
        }
        Program targetProgram = programRepository.findById(applyRequest.getProgramId())
                .orElseThrow(NoSuchElementException::new);
        participantRepository.save(applyRequest.toEntity(targetProgram));
    }

    @Transactional
    public List<ApplyRequestDto> getApplicants(Long programId) {
        return participantRepository.findAllApplicantByProgramId(programId);
    }

    @Transactional
    public boolean isHost(Long programId, Long memberId) {
        return participantRepository.isHost(programId, memberId) > 0 ? true : false;
    }

    @Transactional
    public void approval(Long programId, Long memberId) {
        Participant participant = participantRepository.findParticipantByProgramIdAndMemberId(programId, memberId)
                .orElseThrow(NoSuchElementException::new);
        participant.approve();
    }

    @Transactional
    public void reject(Long programId, Long memberId) {
        Participant participant = participantRepository.findParticipantByProgramIdAndMemberId(programId, memberId)
                .orElseThrow(NoSuchElementException::new);
        participantRepository.delete(participant);
    }

    public Long countCurrentMember(Long programId) {
        return participantRepository.countCurrentMember(programId);
    }

}
