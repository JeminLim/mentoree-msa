package com.mentoree.mentoring.api.controller;

import com.mentoree.common.advice.exception.BindingFailureException;
import com.mentoree.common.advice.exception.NoAuthorityException;
import com.mentoree.mentoring.dto.BoardInfoDto;
import com.mentoree.mentoring.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {

    private final BoardService boardService;

    @GetMapping("/{boardId}")
    public ResponseEntity getBoardInfo(@PathVariable("boardId") long boardId) {
        BoardInfoDto boardInfo = boardService.getBoardInfo(boardId);
        Map<String, Object> data = new HashMap<>();
        data.put("boardInfo", boardInfo);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/new")
    public ResponseEntity createBoard(@Validated @RequestBody BoardInfoDto createRequest,
                                      BindingResult bindingResult) {
        Long memberId = createRequest.getWriterId();
        if(bindingResult.hasErrors()) {
            throw new BindingFailureException(bindingResult, "잘못된 게시글 작성 요청입니다.");
        }
        if(!boardService.isParticipation(memberId)) {
            throw new NoAuthorityException("참가자가 아닙니다.");
        }
        boardService.writeBoard(createRequest);
        Map<String, Object> data = new HashMap<>();
        data.put("result", "success");
        return ResponseEntity.ok().body(data);
    }

}
