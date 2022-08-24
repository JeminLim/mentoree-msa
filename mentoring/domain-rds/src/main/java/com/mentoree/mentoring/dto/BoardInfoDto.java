package com.mentoree.mentoring.dto;

import com.mentoree.mentoring.domain.entity.Board;
import com.mentoree.mentoring.domain.entity.Mission;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class BoardInfoDto implements Serializable {

    private Long boardId;

    @NotNull
    private Long missionId;

    @NotNull
    private String missionTitle;

    @NotNull
    private Long writerId;
    @NotNull
    private String writerNickname;

    @NotNull
    private String content;

    @Builder
    public BoardInfoDto(Long boardId, Long missionId, String missionTitle, String content, Long writerId, String writerNickname) {
        this.boardId = boardId;
        this.missionId = missionId;
        this.missionTitle = missionTitle;
        this.writerId = writerId;
        this.content = content;
        this.writerNickname = writerNickname;
    }

    public Board toEntity(Mission mission) {
        return Board.builder()
                .mission(mission)
                .memberId(writerId)
                .nickname(writerNickname)
                .content(content)
                .build();
    }



}
