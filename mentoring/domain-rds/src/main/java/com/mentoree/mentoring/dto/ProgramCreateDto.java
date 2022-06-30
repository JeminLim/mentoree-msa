package com.mentoree.mentoring.dto;

import com.mentoree.common.domain.Category;
import com.mentoree.common.domain.DataTransferObject;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.entity.ProgramRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ProgramCreateDto extends DataTransferObject {


    /** Program */
    @NotBlank
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String goal;
    @Range(min = 2, max = 10)
    private Integer maxMember;
    @NotNull
    private String category;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate dueDate;


    /** Participant */
    @NotNull
    private Long memberId;
    @NotNull
    private String memberNickname;
    @NotNull
    private Boolean mentor;

    @Builder
    public ProgramCreateDto(String title, Integer maxMember, String goal
            , String description, String category, Boolean mentor, LocalDate dueDate,
                            Long memberId, String memberNickname) {
        this.title = title;
        this.maxMember = maxMember;
        this.goal = goal;
        this.description = description;
        this.category = category;
        this.mentor = mentor;
        this.dueDate = dueDate;
        this.memberId = memberId;
        this.memberNickname = memberNickname;
    }

    public Program toProgramEntity() {
        return Program.builder()
                .title(title)
                .description(description)
                .goal(goal)
                .maxMember(maxMember)
                .category(Category.valueOf(category))
                .dueDate(dueDate)
                .build();
    }

    public Participant toParticipantEntity(Program program) {
        return Participant.builder()
                .memberId(memberId)
                .nickname(memberNickname)
                .role(mentor ? ProgramRole.MENTOR : ProgramRole.MENTEE)
                .program(program)
                .approval(true)
                .isHost(true)
                .build();
    }


}
