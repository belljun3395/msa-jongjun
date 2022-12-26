package com.example.web.exception;

import lombok.Getter;

@Getter
public class GroupValidateException extends RuntimeException {

    private final int code;

    public GroupValidateException(GroupValidateError groupValidateError) {
        super(groupValidateError.getMessage());
        this.code = groupValidateError.getCode();
    }

}
