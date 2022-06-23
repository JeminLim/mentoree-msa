package com.mentoree.mentoring.service;

import com.mentoree.mentoring.domain.entity.Board;
import com.mentoree.mentoring.dto.BoardInfoDto;
import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.domain.repository.BoardRepository;
import com.mentoree.mentoring.domain.repository.MissionRepository;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.messagequeue.connect.ConnectProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;
    private final ConnectProducer connectProducer;

    @Value("${kafka.topics.boards}")
    private String boardConnectTopic;

    @Transactional(readOnly = true)
    public List<BoardInfoDto> getBoardList(Long missionId) {
        return boardRepository.getBoardListByMissionId(missionId);
    }

    @Transactional(readOnly = true)
    public BoardInfoDto getBoardInfo(Long boardId) {
        return boardRepository.getBoardInfoById(boardId);
    }

    @Transactional
    public boolean isParticipation(Long memberId) {
        return participantRepository.countParticipationByMemberId(memberId) > 0;
    }

    @Transactional
    public void writeBoard(BoardInfoDto boardInfoDto) {
        Mission mission = missionRepository.findById(boardInfoDto.getMissionId())
                .orElseThrow(NoSuchElementException::new);
        Board toEntity = boardInfoDto.toEntity(mission);
        boardRepository.save(toEntity);
        connectProducer.send(boardConnectTopic, toEntity);
    }

}
