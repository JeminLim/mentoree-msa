package com.mentoree.common.kafka.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataBaseType {

    STRING("String", "string"),
    INT32("int", "int32"),
    FLOAT64("double", "float64"),
    BOOLEAN("boolean", "boolean");

    private final String key;
    private final String value;


}
