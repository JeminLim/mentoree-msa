package com.mentoree.member.api.controller;


import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/internal")
public class MemberInternalApiController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseMember getMemberInfo(@PathVariable("memberId") Long memberId) {
        return memberService.getMemberInfo(memberId);
    }


}
