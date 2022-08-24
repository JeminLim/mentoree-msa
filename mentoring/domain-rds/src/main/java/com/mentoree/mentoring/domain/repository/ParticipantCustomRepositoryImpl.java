package com.mentoree.mentoring.domain.repository;

import com.mentoree.common.interenal.ParticipatedProgram;
import com.mentoree.mentoring.domain.entity.*;
import com.mentoree.mentoring.domain.repository.ParticipantCustomRepository;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.mentoree.mentoring.domain.entity.QBoard.*;
import static com.mentoree.mentoring.domain.entity.QBoard.board;
import static com.mentoree.mentoring.domain.entity.QMission.*;
import static com.mentoree.mentoring.domain.entity.QMission.mission;
import static com.mentoree.mentoring.domain.entity.QParticipant.*;
import static com.mentoree.mentoring.domain.entity.QProgram.program;
import static com.querydsl.core.types.dsl.Expressions.*;

public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository {


    private final JPAQueryFactory queryFactory;

    public ParticipantCustomRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}


    @Override
    public List<ApplyRequestDto> findAllApplicantByProgramId(Long programId) {
        List<Participant> queryResult = queryFactory.selectFrom(participant)
                .join(participant.program, program)
                .fetchJoin()
                .where(participant.program.id.eq(programId),
                        participant.approval.eq(false))
                .fetch();

        return queryResult.stream().map(ApplyRequestDto::of).collect(Collectors.toList());

    }

    @Override
    public int countParticipationByMemberId(Long memberId) {
        return queryFactory.select(participant)
                .from(participant)
                .where(participant.memberId.eq(memberId),
                        participant.approval.eq(true))
                .fetch().size();
    }

    @Override
    public Participant findApplicantByMemberIdAndProgramId(Long programId, Long memberId) {
        return queryFactory.selectFrom(participant)
                .where(participant.program.id.eq(programId),
                        participant.memberId.eq(memberId))
                .fetchOne();
    }

    @Override
    public List<ParticipatedProgram> findAllProgramByMemberId(Long memberId) {
        return queryFactory.select(Projections.bean(ParticipatedProgram.class,
                    participant.program.id.as("programId"),
                    participant.program.title
                )).from(participant)
                .where(participant.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public List<Participant> findAllParticipantByProgramId(Long programId) {
        return queryFactory.selectFrom(participant)
                .where(participant.program.id.eq(programId))
                .fetch();
    }

    @Override
    public List<Participant> findAllParticipantByMissionId(Long missionId) {
        Mission findResult = queryFactory.select(mission)
                .from(mission)
                .join(mission.program, program)
                .fetchJoin()
                .where(mission.id.eq(missionId))
                .fetchOne();

        return queryFactory.selectFrom(participant)
                .where(participant.program.id.eq(findResult.getProgram().getId()))
                .fetch();
    }

    @Override
    public List<Participant> findAllParticipantByBoardId(Long boardId) {
        Board findResult = queryFactory.select(board)
                .from(board)
                .join(board.mission, mission)
                .fetchJoin()
                .where(board.id.eq(boardId))
                .fetchOne();

        return queryFactory.selectFrom(participant)
                .where(participant.program.id.eq(findResult.getMission().getProgram().getId()))
                .fetch();
    }

    @Override
    public List<Participant> findAllParticipantByMemberId(Long memberId) {
        return queryFactory.selectFrom(participant)
                .where(participant.memberId.eq(memberId))
                .fetch();
    }

}
