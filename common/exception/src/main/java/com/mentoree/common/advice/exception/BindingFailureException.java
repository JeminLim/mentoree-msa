package com.mentoree.common.advice.exception;

import org.springframework.validation.BindingResult;

public class BindingFailureException extends RuntimeException{

    private String message;
    private final BindingResult bindingResult;

    public BindingFailureException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingFailureException(BindingResult bindingResult, String message) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
