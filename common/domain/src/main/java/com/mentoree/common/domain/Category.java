package com.mentoree.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    EMPTY("EMPTY", "입력없음"),
    IT("IT", "IT/PROGRAMMING"),
    MUSIC("MUSIC", "음악"),
    LIFE("LIFE", "인생상담"),
    EMPLOYMENT("EMPLOYMENT", "취업"),
    ART("ART", "미술");

    private final String key;
    private final String value;
}
