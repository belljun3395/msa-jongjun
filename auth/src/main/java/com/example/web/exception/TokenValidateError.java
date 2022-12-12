package com.example.web.exception;

public enum TokenValidateError {

    ACCESS_TIME_EXCEED(1201, "exceed access token's time period "),
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