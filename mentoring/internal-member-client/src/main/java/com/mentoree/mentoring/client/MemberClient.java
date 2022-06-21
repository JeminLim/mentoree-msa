package com.mentoree.mentoring.client;

import com.mentoree.mentoring.dto.ResponseMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "Member-client", url = "urllllll")
public interface MemberClient {

    @RequestMapping(method = RequestMethod.GET, value = "/{memberId}")
    ResponseMember getMember(@PathVariable("memberId") Long memberId);

}
