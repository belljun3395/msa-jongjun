package com.example.exception;

public class NotValidateTokenExceptionCustom extends CustomZuulException {
    private  int code = 102;

    private String error = "NotValidateTokenException";

    private String errorMessage = "Not validate token";

    public  int getCode() {
        return code;
    }

    public  String getError() {
        return error;
    }

    public  String getErrorMessage() {
        return errorMessage;
    }
}
