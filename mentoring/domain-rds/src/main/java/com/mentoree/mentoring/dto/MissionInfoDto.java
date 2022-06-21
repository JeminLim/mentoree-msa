package com.mentoree.mentoring.dto;

import com.mentoree.common.domain.DataTransferObject;
import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.domain.entity.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MissionInfoDto extends DataTransferObject {

    private Long missionId;

    @NotNull
    private String missionTitle;

    @NotNull
    private String missionGoal;

    @NotNull
    private String content;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public MissionInfoDto(Long missionId, String missionTitle, String content, String missionGoal, LocalDate dueDate) {
        this.missionId = missionId;
        this.missionTitle = missionTitle;
        this.content = content;
        this.missionGoal = missionGoal;
        this.dueDate = dueDate;
    }

    public Mission toEntity(Program program) {
        return Mission.builder()
                .title(missionTitle)
                .goal(missionGoal)
                .content(content)
                .dueDate(dueDate)
                .program(program)
                .build();
    }

}
