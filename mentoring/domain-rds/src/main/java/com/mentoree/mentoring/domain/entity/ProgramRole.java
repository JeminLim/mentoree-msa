package com.mentoree.mentoring.domain.entity;

public enum ProgramRole {

    MENTOR("mentor"),
    MENTEE("mentee");

    private String value;

    ProgramRole(String value) { this.value = value;}

    public String getKey() {
        return name();
    }

    public String getValue() {
        return value;
    }

}
