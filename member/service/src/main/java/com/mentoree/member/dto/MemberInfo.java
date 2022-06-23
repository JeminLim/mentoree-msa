package com.mentoree.member.dto;

import com.mentoree.common.domain.Category;
import com.mentoree.common.domain.DataTransferObject;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.ParticipatedProgram;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfo extends DataTransferObject {

    @NotBlank
    private String email;
    @NotBlank
    private String memberName;
    @NotBlank
    private String nickname;

    private List<String> interests = new ArrayList<>();
    private String link;

    private List<ParticipatedProgram> participatedProgramIdList = new ArrayList<>();

    @Builder
    public MemberInfo(String email, String memberName, String nickname,
                      List<String> interests, String link,
                      List<ParticipatedProgram> participatedProgramIdList) {
        this.email = email;
        this.memberName = memberName;
        this.nickname = nickname;
        this.interests = interests == null ? new ArrayList<>() : interests;
        this.link = link;
        this.participatedProgramIdList = participatedProgramIdList;
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .memberName(memberName)
                .nickname(nickname)
                .link(link)
                .build();
    }

}
