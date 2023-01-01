package com.example.domain.member;


import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MemberLoginInfo {
    private Long memberId;

    private Role role;

    private String clientType;

    private String location;

    @Builder
    public MemberLoginInfo(Long memberId, Role role, String clientType, String location) {
        this.memberId = memberId;
        this.role = role;
        this.clientType = clientType;
        this.location = location;
    }

    public Role getRole() {
        return this.role;
    }
}
