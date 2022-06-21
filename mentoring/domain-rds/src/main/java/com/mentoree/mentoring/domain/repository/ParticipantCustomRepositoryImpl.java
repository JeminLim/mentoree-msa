package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.entity.ProgramRole;
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

import static com.mentoree.mentoring.domain.entity.QParticipant.*;

public class ParticipantCustomRepositoryImpl implements ParticipantCustomRepository {


    private final JPAQueryFactory queryFactory;

    public ParticipantCustomRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}


    @Override
    public List<ApplyRequestDto> findAllApplicantByProgramId(Long programId) {
        return queryFactory.select(Projections.bean(ApplyRequestDto.class,
                        participant.memberId,
                        participant.nickname,
                        participant.program.id.as("programId"),
                        participant.message,
                        Expressions.asString(String.valueOf(participant.role)).as("role")
                )).from(participant)
                .where(participant.program.id.eq(programId),
                        participant.approval.eq(false))
                .fetch();
    }

    @Override
    public int countParticipationByMemberId(Long memberId) {
        return queryFactory.select(participant)
                .from(participant)
                .where(participant.memberId.eq(memberId),
                        participant.approval.eq(true))
                .fetch().size();
    }

    private String enumToString(ProgramRole role) {
        return role.getValue();
    }


}
