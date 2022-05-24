package com.mentoree.entity;

import com.mentoree.enums.Category;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MemberInterest {

    @Id
    @GeneratedValue
    @Column(name = "member_interest_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder
    public MemberInterest(Member member, Category category) {
        this.category = category;
        this.member = member;
    }

}
