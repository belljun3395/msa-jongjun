package com.example.domain.member;

import java.util.Arrays;

public enum Role {
    MEMBER("member"),
    ADMIN("admin"),
    ;


    private static final String ERROR = "[ERROR] ";
    private static final String ERROR_RIGHT_ROLE = ERROR + "please choose right role";

    private final String type;

    Role(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static Role makeRole(String roleString) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getType()
                        .equals(roleString))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(ERROR_RIGHT_ROLE)
                );
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isMember() {
        return this == MEMBER;
    }
}
