package com.mentoree.mentoring.client;

import com.mentoree.common.interenal.ResponseMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "MEMBER-SERVICE")
public interface MemberClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/members/internal/{memberId}")
    ResponseMember getMemberInfo(@PathVariable("memberId") Long memberId);

}
