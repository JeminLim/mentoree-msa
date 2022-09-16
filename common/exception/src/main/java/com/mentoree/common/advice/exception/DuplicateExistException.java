package com.mentoree.common.advice.exception;

public class DuplicateExistException extends RuntimeException{

    private final Class<?> duplicateEntity;

    public DuplicateExistException(Class<?> entityClass, String message) {
        super(message);
        this.duplicateEntity = entityClass;
    }

    /**
     * @return return duplicate entity class
     */
    public Class<?> getDuplicateEntity() {
        return duplicateEntity;
    }
}
