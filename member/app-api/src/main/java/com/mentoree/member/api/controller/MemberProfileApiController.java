package com.mentoree.member.api.controller;

import com.mentoree.common.advice.exception.BindingFailureException;
import com.mentoree.common.advice.exception.NoAuthorityException;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.dto.MemberInfo;
import com.mentoree.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberProfileApiController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity getMemberProfile(@RequestParam("memberId") Long memberId) {

        log.info("Request endpoint : GET /api/members/profile?memberId=" + memberId);

        MemberInfo memberInfo = memberService.getMemberProfile(memberId);
        return ResponseEntity.ok().body(memberInfo);
    }

    @PostMapping("/profile")
    public ResponseEntity updateMemberProfile(HttpServletRequest request,
                                              @Validated @RequestBody MemberInfo updatedInfo,
                                              BindingResult bindingResult) {

        log.info("Request endpoint : POST /api/members/profile");

        // Request 검증
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult);
        }

        // 사용자 검증
        Long loginMemberId = Long.parseLong(request.getHeader("X-Authorization-Id"));

        log.info("Login member Id = {}", loginMemberId);
        log.info("UpdateForm id = {}", updatedInfo.getMemberId());

        if(loginMemberId != updatedInfo.getMemberId()) {
            throw new NoAuthorityException("해당 회원에게 권한이 없는 요청입니다.");
        }

        log.info("Update Form - interest[0] = {}", updatedInfo.getInterests().get(0));
        log.info("Update Form - interest[1] = {}", updatedInfo.getInterests().get(1));
        log.info("Update Form - interest[2] = {}", updatedInfo.getInterests().get(2));

        MemberInfo updated = memberService.updateMemberProfile(updatedInfo);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("memberInfo", updated);
        return ResponseEntity.ok().body(result);
    }


}
