package com.mentoree.member.dto;

import com.mentoree.member.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfo implements Serializable {

    @NotNull
    private Long memberId;
    @NotBlank
    private String email;
    @NotBlank
    private String memberName;
    @NotBlank
    private String nickname;

    private List<String> interests = new ArrayList<>();
    private String link;


    @Builder
    public MemberInfo(Long memberId, String email, String memberName, String nickname,
                      List<String> interests, String link) {
        this.memberId = memberId;
        this.email = email;
        this.memberName = memberName;
        this.nickname = nickname;
        this.interests = interests == null ? new ArrayList<>() : interests;
        this.link = link;
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .memberName(memberName)
                .nickname(nickname)
                .link(link)
                .build();
    }

    public static MemberInfo of(Member member) {

        return MemberInfo.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .nickname(member.getNickname())
                .interests(
                        member.getInterest().size() > 0 ?
                        member.getInterest().stream().map(i -> i.getCategory().getKey()).collect(Collectors.toList()) :
                        new ArrayList<>()
                ).link(member.getLink())
                .build();

    }

}
