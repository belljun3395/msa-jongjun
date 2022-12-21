package com.example.exception;

import lombok.Getter;

@Getter
public class NotAllowedAPIExceptionCustom extends CustomZuulException {
    private int code = 101;

    private String error = "NotAllowedApi";

    private String errorMessage = "Not Allowed Api";


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