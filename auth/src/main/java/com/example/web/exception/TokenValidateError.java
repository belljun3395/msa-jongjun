package com.example.web.exception;

public enum TokenValidateError {

    NO_TOKEN_LOG(1201, "there are no token log please login again"),
    NOT_MATCH_ROLE(1202, "can't access API, because not allowed Role"),
    ;

    private String message;
    private int code;

    TokenValidateError(int code,String message) {
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