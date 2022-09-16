package com.mentoree.common.advice.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    /**
     * U - User 관련 코드 (auth, token 문제 등)
     * D - Data 관련 코드 (DataBinding, Param 문제 등)
     */
    BAD_CREDENTIALS(401, "U001", " Bad Credentials"),
    INVALID_TOKEN(401, "U002", " Invalid token was used"),
    NO_AUTHORITY(401, "U003", " non-authority user access"),
    EXPIRED_TOKEN(401, "U004", " Expired token was used"),

    ILLEGAL_PARAMS(400, "D001", " Illegal argument binding"),
    ILLEGAL_STATEMENT(400, "D002", " Entity cannot perform method due to statement"),
    ;

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
