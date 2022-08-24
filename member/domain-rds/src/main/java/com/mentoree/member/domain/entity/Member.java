package com.mentoree.member.domain.entity;

import com.mentoree.common.domain.BaseTimeEntity;
import com.mentoree.common.domain.Category;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    /**
     * oauth의 특성으로 각 계정마다 고유 번호를 이용해 식별
     */
    @Column
    private String authId;

    @Enumerated(EnumType.STRING)
    @Column
    private UserRole role;
    @Column
    private String memberName;
    @Column
    private String email;
    @Column
    private String nickname;
    @Column
    private String link;

    @OneToMany(mappedBy = "member", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberInterest> interest = new ArrayList<MemberInterest>();

    @Builder
    public Member(String memberName, String email, String authId, String nickname,
                  String link, UserRole role) {
        this.memberName = memberName;
        this.authId = authId;
        this.email = email;
        this.nickname = nickname;
        this.link = link;
        this.role = role;
    }

    public Member updateMemberName(String memberName) {
        this.memberName = memberName;
        return this;
    }

    public void addInterest(List<MemberInterest> list) {
        this.interest.clear();
        this.interest.addAll(list);
    }

    public void updateNickname(String nickname) { this.nickname = nickname;}

    public void updateLink(String link) { this.link = link;}

}
