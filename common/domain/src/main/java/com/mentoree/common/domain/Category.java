package com.mentoree.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    EMPTY( "입력없음", "EMPTY"),
    IT( "프로그래밍", "IT"),
    MUSIC("음악", "MUSIC" ),
    LIFE("인생상담", "LIFE" ),
    EMPLOYMENT( "취업", "EMPLOYMENT"),
    ART("미술", "ART");

    private final String key;
    private final String value;

}
