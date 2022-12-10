package com.example.web.exception;

public enum MemberValidateError {

    EXIST_MEMBER(1101, "there are already our member"),
    NO_EXIST_MEMBER(1102, "there are no exist member information"),
    ALREADY_ADMIN(1103, "this member is already admin"),
    ALREADY_MEMBER(1104, "this member is already member"),
    ;

    private String message;
    private int code;

    MemberValidateError(int code,String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
