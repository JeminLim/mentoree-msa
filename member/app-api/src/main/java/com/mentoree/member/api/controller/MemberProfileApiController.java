package com.mentoree.member.api.controller;

import com.mentoree.member.dto.MemberInfo;
import com.mentoree.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberProfileApiController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ResponseEntity getMemberProfile(@RequestParam("email") String email) {
        MemberInfo memberInfo = memberService.getMemberProfile(email);
        return ResponseEntity.ok().body(memberInfo);
    }

    @PostMapping("/profile")
    public ResponseEntity updateMemberProfile(@Validated @RequestBody MemberInfo updatedInfo,
                                              BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.error("바인딩 에러 발생");
        }
        memberService.updateMemberProfile(updatedInfo);
        return ResponseEntity.ok().body("Updated");
    }

}
