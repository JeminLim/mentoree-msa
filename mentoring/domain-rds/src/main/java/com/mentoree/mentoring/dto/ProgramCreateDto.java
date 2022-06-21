package com.mentoree.mentoring.dto;

import com.mentoree.common.domain.Category;
import com.mentoree.common.domain.DataTransferObject;
import com.mentoree.mentoring.domain.entity.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ProgramCreateDto extends DataTransferObject {

    @NotNull
    private Long memberId;

    @NotBlank
    private String programName;

    @Range(min = 2, max = 10)
    private Integer targetNumber;

    @NotNull
    private String goal;

    @NotNull
    private String description;

    @NotNull
    private String category;

    @NotNull
    private Boolean mentor;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate dueDate;

    private String programRole; // 멘토여부에 따라서 멘토 아니면 전부 멘티

    @Builder
    public ProgramCreateDto(String programName, Integer targetNumber, String goal
            , String description, String programRole, String category, Boolean mentor, LocalDate dueDate) {
        this.programName = programName;
        this.targetNumber = targetNumber;
        this.goal = goal;
        this.description = description;
        this.programRole = programRole;
        this.category = category;
        this.mentor = mentor;
        this.dueDate = dueDate;
    }

    public Program toEntity(Category category) {
        return Program.builder()
                .programName(programName)
                .description(description)
                .maxMember(targetNumber)
                .goal(goal)
                .category(category)
                .dueDate(dueDate)
                .build();
    }


}
