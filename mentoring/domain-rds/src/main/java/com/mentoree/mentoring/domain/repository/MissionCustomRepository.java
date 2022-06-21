package com.mentoree.mentoring.domain.repository;

import com.mentoree.mentoring.dto.MissionInfoDto;

import java.util.List;

public interface MissionCustomRepository {

    List<MissionInfoDto> getMissionListBy(Long programId, boolean isOpen);
    MissionInfoDto getMissionInfoById(Long missionId);

}
