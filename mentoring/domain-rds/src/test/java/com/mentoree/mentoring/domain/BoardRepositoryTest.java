package com.mentoree.mentoring.domain;

import com.mentoree.mentoring.common.DataPreparation;
import com.mentoree.mentoring.domain.entity.Board;
import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.BoardRepository;
import com.mentoree.mentoring.dto.BoardInfoDto;
import com.mentoree.mentoring.dto.MissionInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private DataPreparation dataPreparation;

    Map<String, Object> entity;

    @BeforeEach
    void init() {
        entity = dataPreparation.getEntity();
    }

    @Test
    @DisplayName("수행보드_리스트_가져오기_테스트")
    void 수행보드_리스트_가져오기_테스트() {
        //given
        Mission mission = (Mission) entity.get("missionA");
        //when
        List<BoardInfoDto> result = boardRepository.getBoardListByMissionId(mission.getId());
        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("수행보드_상세정보_불러오기_테스트")
    void 수행보드_상세정보_불러오기_테스트() {
        //given
        Board board = (Board) entity.get("boardA");
        //when
        BoardInfoDto boardInfo = boardRepository.getBoardInfoById(board.getId());
        //then
        assertThat(boardInfo.getBoardId()).isEqualTo(board.getId());
        assertThat(boardInfo.getMissionId()).isEqualTo(board.getMission().getId());
        assertThat(boardInfo.getContent()).isEqualTo(board.getContent());
        assertThat(boardInfo.getMissionTitle()).isEqualTo(board.getMission().getTitle());
        assertThat(boardInfo.getWriterId()).isEqualTo(board.getMemberId());
    }


}
