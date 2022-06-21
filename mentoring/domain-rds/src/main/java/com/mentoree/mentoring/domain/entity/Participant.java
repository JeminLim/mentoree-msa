package com.mentoree.mentoring.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Participant extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "participant_id")
    private Long id;

    private Long memberId;
    private String nickname;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Enumerated(EnumType.STRING)
    private ProgramRole role;

    private boolean isHost;

    private boolean approval;

    private String message;

    private LocalDateTime endDate;

    @Builder
    public Participant(Long memberId, String nickname, Program program, ProgramRole role, boolean isHost, boolean approval, String message) {
        Assert.notNull(memberId, "participant user must not be null");
        Assert.notNull(program, "participant program must not be null");
        Assert.notNull(role, "participant role must not be null");
        Assert.notNull(isHost, "isHost variable must not be null");

        this.memberId = memberId;
        this.nickname = nickname;
        this.program = program;
        this.role = role;
        this.isHost = isHost;
        this.approval = approval;
        this.message = message;
    }

    public void approve() {
        approval = true;
        int restSeat = program.addMember(this);
        if (restSeat >= program.getMaxMember()) program.closeProgram();
    }

}
