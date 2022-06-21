package com.mentoree.mentoring.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseMember {

    private Long memberId;
    private String nickname;
    private List<String> interests;

}
