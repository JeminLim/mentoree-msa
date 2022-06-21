package com.mentoree.mentoring.api.controller;

import com.mentoree.mentoring.domain.dto.BoardInfoDto;
import com.mentoree.mentoring.domain.dto.MissionInfoDto;
import com.mentoree.mentoring.service.BoardService;
import com.mentoree.mentoring.service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionApiController {

    private final MissionService missionService;
    private final BoardService boardService;

    @GetMapping("/missions/list")
    public ResponseEntity getMissionList(@RequestParam("programId") long programId,
                                         @RequestParam(value = "isOpen", defaultValue = "true") boolean isOpen) {

        List<MissionInfoDto> currentMission = missionService.getMissionList(programId, isOpen);

        Map<String, Object> data = new HashMap<>();
        data.put("missions", currentMission);
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/missions/{missionId}")
    public ResponseEntity getMissionInfo(@PathVariable("missionId") long missionId) {

        MissionInfoDto findMission = missionService.getMissionInfo(missionId);
        List<BoardInfoDto> findBoards = boardService.getBoardList(missionId);

        Map<String, Object> data = new HashMap<>();
        data.put("mission", findMission);
        data.put("boardList", findBoards);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/missions/new")
    public ResponseEntity createMission(@RequestParam("programId") Long programId,
                                        @RequestBody MissionInfoDto missionDTO,
                                        BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
//            throw new BindingFailureException(bindingResult, "잘못된 미션 작성 요청입니다.");
        }

        missionService.enrollMission(programId, missionDTO);

        Map<String, Object> data = new HashMap<>();
        data.put("result", "success");
        return ResponseEntity.ok().body(data);

    }

}
