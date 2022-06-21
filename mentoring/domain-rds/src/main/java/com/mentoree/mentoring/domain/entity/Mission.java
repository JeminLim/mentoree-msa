package com.mentoree.mentoring.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Mission extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    private String goal;
    private String title;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public Mission(Program program, String title, String content, LocalDate dueDate, String goal) {
        Assert.notNull(program, "program must not be null");
        Assert.notNull(title, "title must not be null");
        Assert.notNull(content, "content must not be null");
        Assert.notNull(dueDate, "dueDate must not be null");

        this.program = program;
        this.title = title;
        this.content = content;
        this.goal = goal;
        this.dueDate = dueDate;
    }


}
