package com.mentoree.member.domain.entity;

import com.mentoree.common.domain.Category;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javax.persistence.FetchType.*;

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
    @Column
    private UserRole role;
    @Column
    private String memberName;
    @Column
    private String email;
    @Column
    private String oAuth2Id;
    @Column
    private String nickname;
    @Column
    private String link;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ParticipatedProgram> participatedPrograms = new ArrayList<>();

    @ElementCollection(fetch = LAZY)
    private List<MemberInterest> interest = new ArrayList<>();

    @Builder
    public Member(String memberName, String email, String oAuth2Id, String nickname,
                  String link, UserRole role) {
        this.memberName = memberName;
        this.oAuth2Id = oAuth2Id;
        this.email = email;
        this.nickname = nickname;
        this.link = link;
        this.role = role;
        IntStream.range(0, 3).forEach(index -> interest.add(new MemberInterest(Category.EMPTY)));
    }

    public Member updateMemberName(String memberName) {
        this.memberName = memberName;
        return this;
    }

    public void updateInterest(List<String> interestList) {
        IntStream.range(0, interest.size()).forEach(index -> {
            interest.get(index).updateCategory(Category.valueOf(interestList.get(index)));
        });
    }

    public void updateNickname(String nickname) { this.nickname = nickname;}

    public void updateLink(String link) { this.link = link;}

    public void participated(ParticipatedProgram program) {
        this.participatedPrograms.add(program);
    }


}
