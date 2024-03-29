package com.mentoree.common.jwt.exception;

public class InvalidTokenException extends RuntimeException {

    private final String code;
    private String message;

    public InvalidTokenException(String code, String message) {
        super(message);
        this.code = code;
    }

}
