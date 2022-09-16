package com.mentoree.reply.domain.repository;

import com.mentoree.reply.domain.dto.ReplyDto;
import com.mentoree.reply.domain.entity.QReply;
import com.mentoree.reply.domain.repository.CustomReplyRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mentoree.reply.domain.entity.QReply.*;

public class CustomReplyRepositoryImpl implements CustomReplyRepository {

    private final JPAQueryFactory queryFactory;

    public CustomReplyRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ReplyDto> findAllReplyDtoByBoardId(Long boardId) {
        return queryFactory.select(Projections.bean(ReplyDto.class,
                        reply.id.as("replyId"),
                        reply.boardId,
                        reply.memberId.as("writerId"),
                        reply.nickname.as("writerNickname"),
                        reply.content,
                        reply.modifiedDate
                ))
                .from(reply)
                .where(reply.boardId.eq(boardId))
                .fetch();
    }
}
