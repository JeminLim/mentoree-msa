package com.mentoree.member.domain.entity;


import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ParticipatedProgram {

    @Id
    @GeneratedValue
    @Column(name = "participated_program_id")
    private Long id;

    private Long programId;
    private String programTitle;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ParticipatedProgram(String programTitle, Long programId, Member member) {
        this.programTitle = programTitle;
        this.member = member;
        this.programId = programId;
    }

}
