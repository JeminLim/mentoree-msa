package com.mentoree.mentoring.api.controller;

import com.mentoree.common.advice.exception.BindingFailureException;
import com.mentoree.common.advice.exception.NoAuthorityException;
import com.mentoree.mentoring.client.MemberClient;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.mentoree.mentoring.dto.ParticipatedProgramDto;
import com.mentoree.mentoring.dto.ProgramCreateDto;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.mentoring.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new BindingFailureException(bindingResult, "잘못된 프로그램 생성 요청입니다.");
        }
        ParticipatedProgramDto data = programService.createProgram(createForm);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("participatedProgram", data);
        return ResponseEntity.ok().body(responseBody);
    }

    //== 프로그램 리스트 ==//
    @GetMapping("/list")
    public ResponseEntity getMoreList(HttpServletRequest request, @RequestParam("page") Integer page) {
        String memberId = request.getHeader("X-Authorization-Id");
        Map<String, Object> responseBody = memberId != null ?
                programService.getProgramList(page, Long.parseLong(memberId)) :
                programService.getProgramList(page, null);
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/recommendations/list")
    public ResponseEntity getMoreRecommendList(HttpServletRequest request, @RequestParam("page") Integer page) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        /** 로그인 사용자 정보 Feign client 요청 */
        ResponseMember memberInfo = memberClient.getMemberInfo(memberId);
        Slice<ProgramInfoDto> recommendProgramList
                = programService.getRecommendProgramList(page, memberId, memberInfo.getInterests());
        Map<String, Object> result = new HashMap<>();
        result.put("recommendProgramList", recommendProgramList.getContent());
        result.put("hasNext", recommendProgramList.hasNext());
        return ResponseEntity.ok().body(result);
    }

    //== 프로그램 상세 정보 ==//
    @GetMapping("/{programId}")
    public ResponseEntity programInfoGet(HttpServletRequest request, @PathVariable("programId") long programId) {
        String authHeader = request.getHeader("X-Authorization-Id");
        Long memberId = authHeader != null ? Long.parseLong(authHeader) : null;
        Boolean isHost = memberId != null && programService.isHost(programId, memberId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("programInfo", programService.getProgramInfo(programId));
        responseBody.put("isHost", isHost);
        return ResponseEntity.ok().body(responseBody);
    }


    //== 프로그램 참가 신청 ==//
    @PostMapping("/{programId}/join")
    public ResponseEntity applyProgram(
            @PathVariable("programId") Long programId,
            @Validated @RequestBody ApplyRequestDto applyRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 참가 신청 요청입니다.");
        }
        programService.applyProgram(applyRequest);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", "success");
        return ResponseEntity.ok().body(responseBody);
    }

    //== 프로그램 참가자 관리 ==//
    @GetMapping("/{programId}/applicants")
    public ResponseEntity programApplicationListGet(HttpServletRequest request, @PathVariable("programId") Long programId) {
        long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        if(!isHost(programId, memberId)) {
            throw new NoAuthorityException("권한이 없는 유저 요청 입니다.");
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
    public ResponseEntity applicantAccept(HttpServletRequest request,
                                          @RequestBody ApplyRequestDto member,
                                          @PathVariable("programId") Long programId) {
        long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        if(!isHost(programId, memberId)) {
            throw new NoAuthorityException("권한이 없는 유저 요청 입니다.");
        }
        programService.approval(programId, member.getMemberId());
        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

    //== 프로그램 참가 거절 ==//
    @PostMapping("/{programId}/applicants/reject")
    public ResponseEntity applicantReject(HttpServletRequest request,
                                          @RequestBody ApplyRequestDto member,
                                          @PathVariable("programId") Long programId) {
        long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        if(!isHost(programId, memberId)) {
            throw new NoAuthorityException("권한이 없는 유저 요청 입니다.");
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
