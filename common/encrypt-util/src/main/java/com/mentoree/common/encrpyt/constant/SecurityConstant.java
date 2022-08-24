package com.mentoree.common.encrpyt.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityConstant {

    ENCRYPT_KEY("ENC_KEY", "McQfTjWmZq4t7w!z%C*F-JaNdRgUkXp2");

    private final String key;
    private final String value;

}
