package com.mentoree.common.jwt.exception;

public class ExpiredTokenException extends RuntimeException{

    private final String code;
    private String message;

    public ExpiredTokenException(String code, String message) {
        super(message);
        this.code = code;
    }

}
