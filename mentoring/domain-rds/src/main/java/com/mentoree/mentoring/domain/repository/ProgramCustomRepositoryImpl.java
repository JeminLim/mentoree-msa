package com.mentoree.mentoring.domain.repository;

import com.mentoree.common.domain.Category;
import com.mentoree.common.domain.RepositoryHelper;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.entity.QProgram;
import com.mentoree.mentoring.domain.repository.ProgramCustomRepository;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mentoree.mentoring.domain.entity.QParticipant.*;
import static com.mentoree.mentoring.domain.entity.QProgram.*;
import static com.querydsl.core.group.GroupBy.list;

public class ProgramCustomRepositoryImpl implements ProgramCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ProgramCustomRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Slice<ProgramInfoDto> findAllProgram(Pageable pageable, List<Long> programIds) {

        /** OneToMany collection fetch join paging 문제 발생
         *  One 을 기준으로 쿼리를 날리면서 발생하는 N+1 문제와 페이징 불가 문제
         *  Many 관계는 우선 뺴고, 쿼리 실행
         *  -> Lazy loading 시점에 컬렉션 정보를 조회
         *  -> Hibernate 의 default_batch_fetch_size 설정으로 컬렉션에 있는 조회 쿼리를 in 쿼리로 최적화
         *  -> 페이징이 적용되서 가져온 Program 객체를 in 에 집어넣어서 한 번에 연관된 객체들을 조회
         *  -> 1+1 쿼리가 결과적으로 적용
         */
        // 우선 프로그램을 페이징으로 조회(Program 에서 ManyToOne 관계 객체는 페치 조인으로 최적화)
        List<Program> programs = queryFactory.selectFrom(program)
                .where( notInParticipatedPrograms(programIds),
                        program.isOpen.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        // OneToMany 관계에 있는 Participant 컬렉션이 필요한 시점에서, batch size 를 지정함으로써 in 쿼리로 한번에 조회
        List<ProgramInfoDto> result = programs.stream()
                .map(ProgramInfoDto::of).collect(Collectors.toList());

        return RepositoryHelper.toSlice(result, pageable);
    }

    @Override
    public Slice<ProgramInfoDto> findRecommendProgram(Pageable pageable, List<Long> programIds, List<Category> interests) {
        List<Program> programs = queryFactory.selectFrom(program)
                .where( notInParticipatedPrograms(programIds),
                        matchInterestCategory(interests),
                        program.isOpen.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<ProgramInfoDto> result = programs.stream()
                .map(ProgramInfoDto::of).collect(Collectors.toList());

        return RepositoryHelper.toSlice(result, pageable);
    }

    @Override
    public Optional<ProgramInfoDto> findProgramInfoById(Long programId) {
        Program findProgram = queryFactory.selectFrom(program)
                .join(program.participants, participant)
                .fetchJoin()
                .where(program.id.eq(programId))
                .fetchOne();
        return Optional.ofNullable(ProgramInfoDto.of(findProgram));
    }

    private BooleanExpression notInParticipatedPrograms(List<Long> programIds) {
        return programIds.isEmpty()? null : program.id.notIn(programIds) ;
    }

    private BooleanExpression matchInterestCategory(List<Category> categories) {
        return categories.isEmpty() ? null : program.category.in(categories);
    }

}
