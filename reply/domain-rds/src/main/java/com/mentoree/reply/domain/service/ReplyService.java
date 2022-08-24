package com.mentoree.reply.domain.service;

import com.mentoree.reply.domain.dto.ReplyDto;
import com.mentoree.reply.domain.entity.Reply;
import com.mentoree.reply.domain.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    public List<ReplyDto> getReplies(Long boardId) {
        return replyRepository.findAllReplyDtoByBoardId(boardId);
    }

    public ReplyDto writeReply(ReplyDto replyDto) {
        Reply savedReply = replyRepository.save(replyDto.toEntity());
        return ReplyDto.of(savedReply);
    }

}
