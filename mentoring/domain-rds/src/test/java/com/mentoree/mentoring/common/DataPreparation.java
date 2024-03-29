package com.mentoree.mentoring.common;

import com.mentoree.common.domain.Category;
import com.mentoree.mentoring.domain.entity.*;
import com.mentoree.mentoring.domain.repository.BoardRepository;
import com.mentoree.mentoring.domain.repository.MissionRepository;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DataPreparation {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private BoardRepository boardRepository;

    private final Map<String, Object> entityMap = new HashMap<>();

    public Map<String, Object> getEntity() {
        init();
        return this.entityMap;
    }

    public void init() {

        Program preProgram = Program.builder()
                .goal("testGoal")
                .maxMember(5)
                .dueDate(LocalDate.now().plusDays(5))
                .description("testDesc")
                .category(Category.ART)
                .title("testProgram")
                .build();
        Program savedProgram = programRepository.save(preProgram);

        Program preProgramB = Program.builder()
                .goal("testGoalB")
                .maxMember(5)
                .dueDate(LocalDate.now().plusDays(5))
                .description("testDescB")
                .category(Category.IT)
                .title("testProgramB")
                .build();
        Program savedProgramB = programRepository.save(preProgramB);

        Participant preParticipantA = Participant.builder()
                .isHost(true)
                .memberId(1L)
                .approval(true)
                .role(ProgramRole.MENTOR)
                .program(preProgram)
                .nickname("testNick")
                .build();
        Participant savedParticipantA = participantRepository.save(preParticipantA);

        Participant preParticipantB = Participant.builder()
                .isHost(true)
                .memberId(2L)
                .approval(false)
                .role(ProgramRole.MENTOR)
                .program(preProgram)
                .nickname("testNick")
                .build();
        Participant savedParticipantB = participantRepository.save(preParticipantB);

        Participant preParticipantC = Participant.builder()
                .isHost(true)
                .memberId(1L)
                .approval(false)
                .role(ProgramRole.MENTOR)
                .program(preProgramB)
                .nickname("testNick")
                .build();
        Participant savedParticipantC = participantRepository.save(preParticipantC);

        Mission preMission = Mission.builder()
                .title("testMission")
                .content("testContent")
                .dueDate(LocalDate.now().plusDays(7))
                .program(preProgram)
                .build();
        Mission savedMission = missionRepository.save(preMission);

        Mission preMissionB = Mission.builder()
                .title("testMission")
                .content("testContent")
                .dueDate(LocalDate.now().minusDays(7))
                .program(preProgram)
                .build();
        Mission savedMissionB = missionRepository.save(preMissionB);

        Board preBoard = Board.builder()
                .memberId(2L)
                .mission(preMission)
                .content("testBoard")
                .build();
        Board savedBoard = boardRepository.save(preBoard);

        entityMap.put("programA", savedProgram);
        entityMap.put("programB", savedProgramB);
        entityMap.put("missionA", savedMission);
        entityMap.put("missionB", savedMissionB);
        entityMap.put("boardA", savedBoard);
        entityMap.put("participantA", savedParticipantA);
        entityMap.put("participantB", savedParticipantB);
        entityMap.put("participantC", savedParticipantC);

    }


}
