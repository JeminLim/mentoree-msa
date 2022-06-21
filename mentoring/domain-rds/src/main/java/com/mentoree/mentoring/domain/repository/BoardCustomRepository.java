package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.dto.BoardInfoDto;

import java.util.List;

public interface BoardCustomRepository {

    List<BoardInfoDto> getBoardListByMissionId(Long missionId);
    BoardInfoDto getBoardInfoById(Long boardId);

}
