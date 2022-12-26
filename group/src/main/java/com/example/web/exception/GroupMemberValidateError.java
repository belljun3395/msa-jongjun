package com.example.web.exception;

public enum GroupMemberValidateError {

    NO_PARTICIPATE_GROUP(1101, "no participate group"),
    ALREADY_PARTICIPATED(1102, "already participate this group"),
    NO_MORE_MEMBER(1103, "can't join this group more"),
    NO_MEMBER(1104, "no member"),
    NOT_MATCH_GROUP(1105, "not match group"),
    ;

    private String message;
    private int code;

    GroupMemberValidateError(int code, String message) {
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
