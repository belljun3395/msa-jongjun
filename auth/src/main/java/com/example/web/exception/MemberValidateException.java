package com.example.web.exception;

public class MemberValidateException extends RuntimeException {

    private final int code;

    public MemberValidateException(MemberValidateError memberValidateError) {
        super(memberValidateError.getMessage());
        this.code = memberValidateError.getCode();
    }

}
