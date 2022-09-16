package com.mentoree.mentoring.api.controller;

import com.mentoree.common.advice.exception.BindingFailureException;
import com.mentoree.common.advice.exception.NoAuthorityException;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.ProgramRole;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.dto.BoardInfoDto;
import com.mentoree.mentoring.dto.MissionInfoDto;
import com.mentoree.mentoring.service.BoardService;
import com.mentoree.mentoring.service.MissionService;
import com.mentoree.mentoring.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionApiController {

    private final ProgramService programService;
    private final MissionService missionService;
    private final BoardService boardService;

    @GetMapping("/list")
    public ResponseEntity getMissionList(@RequestParam("programId") long programId,
                                         @RequestParam(value = "isOpen", defaultValue = "true") boolean isOpen) {
        List<MissionInfoDto> currentMission = missionService.getMissionList(programId, isOpen);
        Map<String, Object> data = new HashMap<>();
        data.put("missions", currentMission);
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/{missionId}")
    public ResponseEntity getMissionInfo(@PathVariable("missionId") long missionId) {
        MissionInfoDto findMission = missionService.getMissionInfo(missionId);
        List<BoardInfoDto> findBoards = boardService.getBoardList(missionId);
        Map<String, Object> data = new HashMap<>();
        data.put("mission", findMission);
        data.put("boardList", findBoards);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/new")
    public ResponseEntity createMission(HttpServletRequest request,
                                        @RequestBody MissionInfoDto missionDTO,
                                        BindingResult bindingResult) {
        Long programId = missionDTO.getProgramId();
        long loginMemberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        if(!programService.isMentor(programId, loginMemberId)) {
            throw new NoAuthorityException("멘토가 아닙니다.");
        }
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 미션 작성 요청 입니다.");
        }
        missionService.enrollMission(programId, missionDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("result", "success");
        return ResponseEntity.ok().body(data);
    }

}
