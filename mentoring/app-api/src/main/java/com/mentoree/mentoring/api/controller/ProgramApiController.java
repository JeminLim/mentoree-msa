package com.mentoree.mentoring.api.controller;

import com.mentoree.mentoring.client.MemberClient;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.mentoree.mentoring.dto.ParticipatedProgramDto;
import com.mentoree.mentoring.dto.ProgramCreateDto;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import com.mentoree.mentoring.dto.ResponseMember;
import com.mentoree.mentoring.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/programs")
public class ProgramApiController {

    private final ProgramService programService;
    private final MemberClient memberClient;

    /** 프로그램 생성 */
    @PostMapping("/new")
    public ResponseEntity createProgram(@Validated @RequestBody ProgramCreateDto createForm,
                                        BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.error("바인딩 에러 발생");
        }
        ParticipatedProgramDto data = programService.createProgram(createForm);
        return ResponseEntity.ok().body(data);
    }



    //== 프로그램 리스트 ==//
    @GetMapping("/list")
    public ResponseEntity getMoreList(@RequestParam("page") Integer page, @RequestParam("memberId") Long memberId) {
        ResponseMember member = memberClient.getMember(memberId);
        List<ProgramInfoDto> programList = programService.getProgramList(page, member.getMemberId());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("program", programList);
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/list/recommend")
    public ResponseEntity getMoreRecommendList(@RequestParam("page") Integer page, @RequestParam("memberId") Long memberId) {
        ResponseMember member = memberClient.getMember(memberId);
        List<ProgramInfoDto> recommendProgramList =
                programService.getRecommendProgramList(page, member.getMemberId(), member.getInterests());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("program", recommendProgramList);
        return ResponseEntity.ok().body(responseBody);
    }

    //== 프로그램 상세 정보 ==//
    @GetMapping("/{programId}")
    public ResponseEntity programInfoGet(@PathVariable("programId") long programId) {
        return ResponseEntity.ok().body(programService.getProgramInfo(programId));
    }


    //== 프로그램 참가 신청 ==//
    @PostMapping("/{programId}/join")
    public ResponseEntity applyProgram(@Validated @RequestBody ApplyRequestDto applyRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
//            throw new BindingFailureException(bindingResult, "잘못된 참가 신청 요청입니다.");
            log.error("바인딩 에러 발생");
        }
        programService.applyProgram(applyRequest);
        return ResponseEntity.ok().body("success");
    }

    //== 프로그램 참가자 관리 ==//
    @GetMapping("/{programId}/applicants")
    public ResponseEntity programApplicationListGet(@PathVariable("programId") Long programId, @RequestParam("memberId") Long memberId) {
        if(!isHost(programId, memberId)) {
            log.error("권한이 없습니다.");
        }

        ProgramInfoDto programInfo = programService.getProgramInfo(programId);
        List<ApplyRequestDto> applicants = programService.getApplicants(programId);
        Long curMember = programService.countCurrentMember(programId);

        Map<String, Object> result = new HashMap<>();
        result.put("programInfo", programInfo);
        result.put("applicants", applicants);
        result.put("curNum", curMember);

        return ResponseEntity.ok().body(result);
    }

    //== 프로그램 참가 승인 ==//
    @PostMapping("/{programId}/applicants/accept")
    public ResponseEntity applicantAccept(@RequestBody ApplyRequestDto member,
                                          @PathVariable("programId") Long programId) {
        if(!isHost(programId, member.getMemberId())) {
            log.error("권한이 없습니다.");
        }
        programService.approval(programId, member.getMemberId());

        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

    //== 프로그램 참가 거절 ==//
    @PostMapping("/{programId}/applicants/reject")
    public ResponseEntity applicantReject(@RequestBody ApplyRequestDto member,
                                          @PathVariable("programId") Long programId) {
        // 요청자 해당 프로그램 호스트 판별
        if(!isHost(programId, member.getMemberId())) {
            log.error("권한 없음");
        }
        programService.reject(programId, member.getMemberId());
        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

    private boolean isHost(Long programId, Long memberId) {
        return programService.isHost(programId, memberId);
    }

}
