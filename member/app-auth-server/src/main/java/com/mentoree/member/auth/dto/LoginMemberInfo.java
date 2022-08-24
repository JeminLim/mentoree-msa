package com.mentoree.member.auth.dto;

import com.mentoree.common.interenal.ParticipatedProgram;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginMemberInfo {

    private Long id;
    private String email;
    private String nickname;
    private String memberName;
    private String link;
    private List<String> interests = new ArrayList<>();
    private List<ParticipatedProgram> participatedPrograms = new ArrayList<>();

    @Builder
    public LoginMemberInfo (Long id, String email, String nickname, String memberName,
                            String link, List<String> interests, List<ParticipatedProgram> participatedPrograms) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.memberName = memberName;
        this.link = link;
        this.interests = interests;
        this.participatedPrograms = participatedPrograms;
    }

}
