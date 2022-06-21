package com.mentoree.mentoring.domain;

import com.mentoree.mentoring.common.DataPreparation;
import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.MissionRepository;
import com.mentoree.mentoring.dto.MissionInfoDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private DataPreparation dataPreparation;

    Map<String, Object> entity;

    @BeforeEach
    void init() {
        entity = dataPreparation.getEntity();
    }

    @Test
    @DisplayName("현재_진행중_미션_리스트_가져오기_테스트")
    void 미션_리스트_가져오기_테스트() {
        //given
        Program program = (Program) entity.get("programA");
        //when
        List<MissionInfoDto> result = missionRepository.getMissionListBy(program.getId(), true);
        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미_끝난_미션_리스트_가져오기_테스트")
    void 이미_끝난_미션_리스트_가져오기_테스트() {
        //given
        Program program = (Program) entity.get("programA");
        //when
        List<MissionInfoDto> result = missionRepository.getMissionListBy(program.getId(), false);
        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("미션_상세정보_불러오기_테스트")
    void 미션_상세정보_불러오기_테스트() {
        //given
        Mission mission = (Mission) entity.get("missionA");
        //when
        MissionInfoDto missionInfo = missionRepository.getMissionInfoById(mission.getId());
        //then
        assertThat(missionInfo.getMissionId()).isEqualTo(mission.getId());
        assertThat(missionInfo.getMissionGoal()).isEqualTo(mission.getGoal());
        assertThat(missionInfo.getContent()).isEqualTo(mission.getContent());
        assertThat(missionInfo.getMissionTitle()).isEqualTo(mission.getTitle());
        assertThat(missionInfo.getDueDate()).isEqualTo(mission.getDueDate());
    }
    



}
