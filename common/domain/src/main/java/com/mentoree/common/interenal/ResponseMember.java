package com.mentoree.common.interenal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMember {

    private Long memberId;
    private String nickname;
    private List<String> interests;

}
