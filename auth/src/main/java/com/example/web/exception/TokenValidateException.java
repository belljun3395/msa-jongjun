package com.example.web.exception;

import lombok.Getter;

@Getter
public class TokenValidateException extends RuntimeException {

    private final int code;

    public TokenValidateException(TokenValidateError tokenValidateError) {
        super(tokenValidateError.getMessage());
        this.code = tokenValidateError.getCode();
    }

}
