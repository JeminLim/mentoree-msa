package com.mentoree.member.domain.entity;

import com.mentoree.common.domain.Category;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInterest {

    @Enumerated(EnumType.STRING)
    private Category category;

    public MemberInterest(Category category) {
        this.category = category;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

}
