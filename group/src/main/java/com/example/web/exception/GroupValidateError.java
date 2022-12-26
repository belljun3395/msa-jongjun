package com.example.web.exception;

public enum GroupValidateError {

    NO_SUCH_GROUP(1101, "no such group"),
    NOT_OWNER(1102, "you are not owner"),
    ;

    private String message;
    private int code;

    GroupValidateError(int code, String message) {
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
