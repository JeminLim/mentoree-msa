package com.mentoree.mentoring.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    private Long memberId;
    private String nickname;

    private String content;

    @Builder
    public Board(Mission mission, String content, Long memberId, String nickname) {
        Assert.notNull(mission, "mission must not be null");
        Assert.notNull(content, "content must not be null");
        Assert.notNull(memberId, "writer must not be null");

        this.mission = mission;
        this.content = content;
        this.memberId = memberId;
        this.nickname = nickname;
    }

}
