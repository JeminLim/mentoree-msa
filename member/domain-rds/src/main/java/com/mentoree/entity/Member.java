package com.mentoree.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String memberName;
    private String email;
    private String oAuthId;

    private String nickname;
    private String link;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberInterest> interest = new ArrayList<>();

    @Builder
    public Member(String memberName, String email, String oAuthId, String nickname,
                  String link, UserRole role) {
        this.memberName = memberName;
        this.oAuthId = oAuthId;
        this.email = email;
        this.nickname = nickname;
        this.link = link;
        this.role = role;
    }

}
