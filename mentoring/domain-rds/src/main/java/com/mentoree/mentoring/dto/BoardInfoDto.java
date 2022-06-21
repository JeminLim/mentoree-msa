package com.mentoree.mentoring.dto;

import com.mentoree.common.domain.DataTransferObject;
import com.mentoree.mentoring.domain.entity.Board;
import com.mentoree.mentoring.domain.entity.Mission;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class BoardInfoDto extends DataTransferObject {

    private Long boardId;

    @NotNull
    private Long missionId;

    @NotNull
    private String missionTitle;

    @NotNull
    private Long writerId;

    @NotNull
    private String content;

    @Builder
    public BoardInfoDto(Long boardId, Long missionId, String missionTitle, String content, Long writerId) {
        this.boardId = boardId;
        this.missionId = missionId;
        this.missionTitle = missionTitle;
        this.writerId = writerId;
        this.content = content;
    }

    public Board toEntity(Mission mission) {
        return Board.builder()
                .mission(mission)
                .memberId(writerId)
                .content(content)
                .build();
    }



}
