package com.mentoree.mentoring.api.controller;

import com.mentoree.common.interenal.ParticipatedProgram;
import com.mentoree.mentoring.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/programs/internal")
public class ProgramInternalApiController {

    private final ProgramService programService;

    @GetMapping("/{memberId}")
    public List<ParticipatedProgram> getParticipatedPrograms(@PathVariable("memberId") Long memberId) {
        return programService.getParticipatedPrograms(memberId);
    }

}
