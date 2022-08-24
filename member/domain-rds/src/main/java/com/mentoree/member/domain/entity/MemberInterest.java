package com.mentoree.member.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import com.mentoree.common.domain.Category;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "Member_Interest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInterest extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_interest_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder
    public MemberInterest(Member member, Category category) {
        this.member = member;
        this.category = category;
    }

}
