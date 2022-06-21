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

    private JPAQueryFactory queryFactory;

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
                board.content
                ))
                .from(board)
                .join(board.mission, mission)
                .where(board.mission.id.eq(missionId))
                .fetch();
    }

    @Override
    public BoardInfoDto getBoardInfoById(Long boardId) {

        // fetch join 은 단순 엔티티 그래프 탐색을 가능하게 하기 때문에 Projection 에서 사용할 수 없다.
        // 조인을 한 테이블에서 데이터를 탐색하고 싶으면, 그냥 Join을 사용해서 한꺼번에 가져와야 한다.
        // 만약 join을 안하면, 내부적으로 Cross join을 사용해서 데이터가 불어서 가져오는 불상사가 일어난다.

        return queryFactory.select(Projections.bean(BoardInfoDto.class,
                        board.id.as("boardId"),
                        board.mission.id.as("missionId"),
                        board.mission.title.as("missionTitle"),
                        board.memberId.as("writerId"),
                        board.content
                ))
                .from(board)
                .join(board.mission, mission)
                .where(board.id.eq(boardId))
                .fetchOne();

    }

}
