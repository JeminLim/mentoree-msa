package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.domain.entity.QMission;
import com.mentoree.mentoring.domain.entity.QProgram;
import com.mentoree.mentoring.domain.repository.MissionCustomRepository;
import com.mentoree.mentoring.dto.MissionInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.mentoree.mentoring.domain.entity.QMission.*;
import static com.mentoree.mentoring.domain.entity.QProgram.*;

public class MissionCustomRepositoryImpl implements MissionCustomRepository {

    private final JPAQueryFactory queryFactory;

    public MissionCustomRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<MissionInfoDto> getMissionListBy(Long programId, boolean isOpen) {
        List<Mission> queryResult = queryFactory.selectFrom(mission)
                .join(mission.program, program).fetchJoin()
                .where(mission.program.id.eq(programId),
                        isOpenMission(isOpen))
                .fetch();

        return queryResult.stream().map(MissionInfoDto::of).collect(Collectors.toList());
    }

    @Override
    public MissionInfoDto getMissionInfoById(Long missionId) {
        Mission queryResult = queryFactory.selectFrom(QMission.mission)
                .join(QMission.mission.program, program).fetchJoin()
                .where(QMission.mission.id.eq(missionId))
                .fetchOne();
        return MissionInfoDto.of(queryResult);
    }

    private BooleanExpression isOpenMission(boolean isOpen) {
        LocalDate now = LocalDate.now();
        return isOpen ? mission.dueDate.after(now) : mission.dueDate.before(now);
    }
}
