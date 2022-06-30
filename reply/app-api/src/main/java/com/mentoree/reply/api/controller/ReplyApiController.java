package com.mentoree.reply.api.controller;

import com.mentoree.reply.domain.dto.ReplyDto;
import com.mentoree.reply.domain.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyApiController {

    private final ReplyService replyService;

    @GetMapping("/list")
    public ResponseEntity getReplies(@RequestParam("boardId") long boardId) {
        List<ReplyDto> replies = replyService.getReplies(boardId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("replyList", replies);
        return ResponseEntity.ok().body(responseBody);
    }

    //== 댓글 작성 관련 ==//
    @PostMapping("/new")
    public ResponseEntity replyWrite(@Validated @RequestBody ReplyDto replyCreateForm,
                                     BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
//            throw new BindingFailureException(bindingResult, "잘못된 댓글 작성 요청입니다.");
            log.error("바인딩 에러 발생");
        }

        replyService.writeReply(replyCreateForm);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }



}
