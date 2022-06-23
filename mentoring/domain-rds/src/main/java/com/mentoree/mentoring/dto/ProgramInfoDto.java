package com.mentoree.mentoring.dto;

import com.mentoree.common.domain.Category;
import com.mentoree.common.domain.DataTransferObject;
import com.mentoree.mentoring.domain.entity.Program;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "mentor")
public class ProgramInfoDto extends DataTransferObject {

    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Category category;

    @NotNull
    private int maxMember;

    private List<String> mentor = new ArrayList<>();

    @NotNull
    private String goal;

    @NotNull
    private String description;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public ProgramInfoDto(Long id, String title, Category category, String goal, int maxMember, List<String> mentor, String description, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.maxMember = maxMember;
        this.mentor = mentor;
        this.goal = goal;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Program toEntity() {
        return Program.builder()
                .programName(title)
                .category(category)
                .description(description)
                .dueDate(dueDate)
                .maxMember(maxMember)
                .goal(goal)
                .build();
    }

}
