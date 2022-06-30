package com.mentoree.member.api.controller;


import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.MemberInterest;
import com.mentoree.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberInternalApiController {

    private final MemberService memberService;

    @GetMapping("/internal/{memberId}")
    public ResponseEntity getMemberInfo(@PathVariable("memberId") Long memberId) {
        Member member = memberService.getMemberInfo(memberId);
        List<String> interest = member.getInterest().stream()
                                .map(m -> m.getCategory().getKey())
                                .collect(Collectors.toList());
        ResponseMember result = new ResponseMember(member.getId(), member.getNickname(), interest);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", result);
        return ResponseEntity.ok().body(responseBody);
    }


}
