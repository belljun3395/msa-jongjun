package com.example.domain.member;


import com.example.utils.token.JWTToken;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
public class MemberLoginInfo {

    private final Long NOW = System.currentTimeMillis();
    private final Long ONE_DAY = 24 * 60 * 60 * 1000L;
    private final Long TWENTY_MIN = 20 * 60L * 1000L;

    private final Long REFRESH_TOKEN_EXP = NOW + ONE_DAY;
    private final Long ACCESS_TOKEN_EXP = NOW + TWENTY_MIN;

    private final String MEMBER_ID = "memberId";
    private final String ROLE = "role";

    private Long memberId;

    private Role role;

    private String clientType;

    private String location;

    private String accessToken;

    private String refreshToken;


    @Builder
    public MemberLoginInfo(Long memberId, Role role, String clientType, String location) {
        this.memberId = memberId;
        this.role = role;
        this.clientType = clientType;
        this.location = location;
        this.accessToken = makeAccessToken();
        this.refreshToken = makeAccessToken();
    }

    public Map<String, Object> makeTokenInfo() {
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put(MEMBER_ID, memberId);
        tokenInfo.put(ROLE, role);
        return tokenInfo;
    }

    public Role getRole() {
        return role;
    }

    private String makeAccessToken() {
        return JWTToken.makeToken(new Date(ACCESS_TOKEN_EXP));
    }

    private String makeRefreshToken() {
        return JWTToken.makeToken(new Date(REFRESH_TOKEN_EXP), this.makeTokenInfo());
    }

}
