package com.mentoree.mentoring.domain.repository;

import com.mentoree.common.domain.Category;
import com.mentoree.common.domain.RepositoryHelper;
import com.mentoree.mentoring.domain.repository.ProgramCustomRepository;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.mentoree.mentoring.domain.entity.QParticipant.*;
import static com.mentoree.mentoring.domain.entity.QProgram.*;

public class ProgramCustomRepositoryImpl implements ProgramCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ProgramCustomRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Slice<ProgramInfoDto> findAllProgram(Pageable pageable, List<Long> programIds) {

        List<ProgramInfoDto> programs = queryFactory.select(Projections.bean(ProgramInfoDto.class,
                        program.id,
                        program.programName.as("title"),
                        program.category,
                        program.maxMember,
                        program.goal,
                        program.description,
                        program.dueDate,
                        ExpressionUtils.as(JPAExpressions.select(participant.nickname)
                                            .from(participant)
                                            .where(participant.program.eq(program))
                                            .fetchAll(), "mentor")))
                .from(program)
                .where( notInParticipatedPrograms(programIds),
                        program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        return RepositoryHelper.toSlice(programs, pageable);

    }

    @Override
    public Slice<ProgramInfoDto> findRecommendProgram(Pageable pageable, List<Long> programIds, List<Category> interests) {
        List<ProgramInfoDto> programs = queryFactory.select(Projections.bean(ProgramInfoDto.class,
                        program.id,
                        program.programName.as("title"),
                        program.category,
                        program.maxMember,
                        program.goal,
                        program.description,
                        program.dueDate,
                        ExpressionUtils.as(JPAExpressions.select(participant.nickname)
                                .from(participant)
                                .where(participant.program.eq(program))
                                .fetchAll(), "mentor")))
                .from(program)
                .where( notInParticipatedPrograms(programIds),
                        matchInterestCategory(interests),
                        program.isOpen.eq(true))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();

        return RepositoryHelper.toSlice(programs, pageable);
    }

    @Override
    public Optional<ProgramInfoDto> findProgramInfoById(Long programId) {
        return Optional.ofNullable(queryFactory.select(Projections.bean(ProgramInfoDto.class,
                        program.id,
                        program.programName.as("title"),
                        program.category,
                        program.maxMember,
                        program.goal,
                        program.description,
                        program.dueDate,
                        ExpressionUtils.as(JPAExpressions.select(participant.nickname)
                                .from(participant)
                                .where(participant.program.eq(program))
                                .fetchAll(), "mentor")))
                .from(program)
                .where(program.id.eq(programId)).fetchOne());
    }

    private BooleanExpression notInParticipatedPrograms(List<Long> programIds) {
        return programIds != null ? program.id.notIn(programIds) : null;
    }

    private BooleanExpression matchInterestCategory(List<Category> categories) {
        return categories != null ? program.category.in(categories) : null;
    }

}
