package com.mentoree.mentoring.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgramRole {

    MENTOR("mentor", "멘토"),
    MENTEE("mentee", "멘티");

    private final String key;
    private final String value;

}
