package com.mentoree.mentoring.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import com.mentoree.common.domain.Category;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Program extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "program_id")
    private Long id;

    //변경 가능
    private String programName;
    private String description;
    private String goal;
    private int maxMember;

    private Category category;
    //========================//

    @OneToMany(mappedBy = "program")
    private List<Participant> participants = new ArrayList<>();

    private int curNum;
    private boolean isOpen;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public Program(String programName, String description, int maxMember, String goal, Category category, LocalDate dueDate) {
        Assert.notNull(programName, "program name must not be null");
        Assert.notNull(description, "description must not be null");
        Assert.notNull(goal, "goal must not be null");
        Assert.isTrue(maxMember > 0, "member limit must be greater than 0");

        this.programName = programName;
        this.description = description;
        this.maxMember = maxMember;
        this.goal = goal;
        this.dueDate = dueDate;
        this.category = category;

        // 기본 초기값
        this.isOpen = true;
        this.curNum = 1;
    }

    public void changeProgramName(String title) {
        this.programName = title;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeGoal(String goal) {
        this.goal = goal;
    }

    public void changeCategory(String category) {
        this.category = Category.valueOf(category);
    }

    public void changeMaxMember(int target) {
        this.maxMember = target;
    }

    public void changeDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void closeProgram() {
        this.isOpen = false;
    }

    public int addMember(Participant participant) {
        this.participants.add(participant);
        curNum++;
        return curNum;
    }

}
