package com.mentoree.member.client;

import com.mentoree.common.interenal.ParticipatedProgram;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "MENTORING-SERVICE")
public interface ParticipatedProgramClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/programs/internal/{memberId}")
    List<ParticipatedProgram> getParticipatedPrograms(@PathVariable("memberId") Long memberId);

}
