package com.mentoree.mentoring.service;

import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.dto.MissionInfoDto;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.MissionRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import com.mentoree.mentoring.messagequeue.connect.ConnectProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final ProgramRepository programRepository;
    private final MissionRepository missionRepository;
    private final ConnectProducer connectProducer;

    @Value("${kafka.topics.missions}")
    private String missionKafkaTopic;

    public List<MissionInfoDto> getMissionList(Long programId, boolean isOpen) {
        return missionRepository.getMissionListBy(programId, isOpen);
    }

    public MissionInfoDto getMissionInfo(Long missionId) {
        return missionRepository.getMissionInfoById(missionId);
    }

    public void enrollMission(Long programId, MissionInfoDto missionInfoDto) {
        Program program = programRepository.findById(programId).orElseThrow(NoSuchElementException::new);
        Mission toEntity = missionInfoDto.toEntity(program);
        missionRepository.save(toEntity);
        connectProducer.send(missionKafkaTopic, toEntity);
    }
}
