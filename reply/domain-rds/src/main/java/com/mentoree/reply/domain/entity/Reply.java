package com.mentoree.reply.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "reply_id")
    private Long id;

    private Long boardId;
    private Long memberId;
    private String nickname;
    private String content;

    @Builder
    public Reply(Long boardId, Long memberId, String nickname, String content) {
        this.boardId = boardId;
        this.memberId = memberId;
        this.content = content;
        this.nickname = nickname;
    }

}
