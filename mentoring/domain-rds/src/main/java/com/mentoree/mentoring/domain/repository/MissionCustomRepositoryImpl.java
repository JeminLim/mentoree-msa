package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.domain.repository.MissionCustomRepository;
import com.mentoree.mentoring.dto.MissionInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.mentoree.mentoring.domain.entity.QMission.*;

public class MissionCustomRepositoryImpl implements MissionCustomRepository {

    private final JPAQueryFactory queryFactory;

    public MissionCustomRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<MissionInfoDto> getMissionListBy(Long programId, boolean isOpen) {
        return queryFactory.select(Projections.bean(MissionInfoDto.class,
                    mission.id.as("missionId"),
                    mission.title.as("missionTitle"),
                    mission.goal.as("missionGoal"),
                    mission.content,
                    mission.dueDate
                ))
                .from(mission)
                .where(mission.program.id.eq(programId),
                        isOpenMission(isOpen))
                .fetch();
    }

    @Override
    public MissionInfoDto getMissionInfoById(Long missionId) {
        return queryFactory.select(Projections.bean(MissionInfoDto.class,
                    mission.id.as("missionId"),
                    mission.title.as("missionTitle"),
                    mission.goal.as("missionGoal"),
                    mission.content,
                    mission.dueDate
                ))
                .from(mission)
                .where(mission.id.eq(missionId))
                .fetchOne();
    }

    private BooleanExpression isOpenMission(boolean isOpen) {
        LocalDate now = LocalDate.now();
        return isOpen ? mission.dueDate.after(now) : mission.dueDate.before(now);
    }
}
