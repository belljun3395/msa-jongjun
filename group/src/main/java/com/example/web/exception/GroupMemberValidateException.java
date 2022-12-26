package com.example.web.exception;

import lombok.Getter;

@Getter
public class GroupMemberValidateException extends RuntimeException {

    private final int code;

    public GroupMemberValidateException(GroupMemberValidateError groupMemberValidateError) {
        super(groupMemberValidateError.getMessage());
        this.code = groupMemberValidateError.getCode();
    }

}
