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

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Program extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "program_id")
    private Long id;

    //변경 가능
    private String title;
    private String description;
    private String goal;
    @Column(name = "max_member")
    private int maxMember;

    @Enumerated(EnumType.STRING)
    private Category category;
    //========================//

    @OneToMany(mappedBy = "program", fetch = LAZY)
    private List<Participant> participants = new ArrayList<>();

    private int curNum;
    private boolean isOpen;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Builder
    public Program(String title, String description, int maxMember, String goal, Category category, LocalDate dueDate) {
        Assert.notNull(title, "program name must not be null");
        Assert.notNull(description, "description must not be null");
        Assert.notNull(goal, "goal must not be null");
        Assert.isTrue(maxMember > 0, "member limit must be greater than 0");

        this.title = title;
        this.description = description;
        this.maxMember = maxMember;
        this.goal = goal;
        this.dueDate = dueDate;
        this.category = category;

        // 기본 초기값
        this.isOpen = true;
        this.curNum = 1;
    }

    public void changeTitle(String title) {
        this.title = title;
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
