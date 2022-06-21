package com.mentoree.reply.domain.repository;

import com.mentoree.reply.domain.dto.ReplyDto;

import java.util.List;

public interface CustomReplyRepository {

    List<ReplyDto> findAllReplyDtoByBoardId(Long boardId);

}
