package com.mentoree.common.dto.messagequeue;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMember {

    private Long memberId;
    private String nickname;

    @Builder
    public UpdateMember(Long memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }

}
