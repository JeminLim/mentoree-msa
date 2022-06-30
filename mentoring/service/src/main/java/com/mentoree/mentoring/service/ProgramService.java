package com.mentoree.mentoring.service;

import com.mentoree.common.domain.Category;
import com.mentoree.mentoring.dto.ParticipatedProgramDto;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.mentoree.mentoring.dto.ProgramCreateDto;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import com.mentoree.mentoring.messagequeue.sync.ParticipantSyncProducer;
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

    private final int ALL_PROGRAM_PAGE_SIZE = 8;
    private final int RECOMMEND_PROGRAM_PAGE_SIZE = 8;

//    private final ParticipantSyncProducer producer;

//    @Value("${kafka.topics.participant-sync}")
//    private final String participantSyncTopic;

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
                .countParticipantByMemberIdAndProgramId(applyRequest.getMemberId(), applyRequest.getProgramId());

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
        return participantRepository.isHost(programId, memberId) > 0;
    }

    @Transactional
    public void approval(Long programId, Long memberId) {
        System.out.println("programId = " + programId + " and memberId = " + memberId);

        List<Participant> all = participantRepository.findAll();
        for (Participant participant : all) {
            System.out.println("[participant] id : " + participant.getId());
            System.out.println("[participant] programId : " + participant.getProgram().getId());
            System.out.println("[participant] memberId : " + participant.getMemberId());
            System.out.println("[participant] nickname : " + participant.getNickname());
            System.out.println("[participant] isApproval : " + participant.isApproval());
        }

        Participant participant = participantRepository.findApplicantByMemberIdAndProgramId(programId, memberId);
        participant.approve();
    }

    @Transactional
    public void reject(Long programId, Long memberId) {
        Participant participant = participantRepository.findApplicantByMemberIdAndProgramId(programId, memberId);
        participantRepository.delete(participant);
    }

    @Transactional
    public Long countCurrentMember(Long programId) {
        return participantRepository.countCurrentMember(programId);
    }

}
