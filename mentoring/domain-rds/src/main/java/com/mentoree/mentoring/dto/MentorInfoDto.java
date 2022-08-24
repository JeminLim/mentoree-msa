package com.mentoree.mentoring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorInfoDto implements Serializable {

    private Long memberId;
    private String nickname;

}
