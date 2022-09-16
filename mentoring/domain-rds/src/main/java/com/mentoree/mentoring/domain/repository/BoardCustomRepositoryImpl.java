package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.repository.BoardCustomRepository;
import com.mentoree.mentoring.dto.BoardInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mentoree.mentoring.domain.entity.QBoard.*;
import static com.mentoree.mentoring.domain.entity.QMission.*;

public class BoardCustomRepositoryImpl implements BoardCustomRepository {

    private final JPAQueryFactory queryFactory;

    public BoardCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<BoardInfoDto> getBoardListByMissionId(Long missionId) {
        return queryFactory.select(Projections.bean(BoardInfoDto.class,
                board.id.as("boardId"),
                board.mission.id.as("missionId"),
                board.mission.title.as("missionTitle"),
                board.memberId.as("writerId"),
                board.nickname.as("writerNickname"),
                board.content.as("content")
                ))
                .from(board)
                .join(board.mission, mission)
                .where(board.mission.id.eq(missionId))
                .fetch();
    }

    @Override
    public BoardInfoDto getBoardInfoById(Long boardId) {
        return queryFactory.select(Projections.bean(BoardInfoDto.class,
                        board.id.as("boardId"),
                        board.mission.id.as("missionId"),
                        board.mission.title.as("missionTitle"),
                        board.memberId.as("writerId"),
                        board.nickname.as("writerNickname"),
                        board.content
                ))
                .from(board)
                .join(board.mission, mission)
                .where(board.id.eq(boardId))
                .fetchOne();

    }

}
