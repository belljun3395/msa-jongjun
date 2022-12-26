package com.example.domain.member;


import com.example.utils.token.JwtToken;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MemberLoginInfo {

    private final Long ONE_DAY = 24 * 60 * 60 * 1000L;
    private final Long TWENTY_MIN = 20 * 60L * 1000L;

    private final String MEMBER_ID = "memberId";
    private final String ROLE = "role";

    private final String UUID_KEY = "uuid";

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
        this.refreshToken = makeRefreshToken();
    }

    public Role getRole() {
        return this.role;
    }

    private String makeAccessToken() {
        long ACCESS_TOKEN_EXP = System.currentTimeMillis() + TWENTY_MIN;
        return JwtToken.makeToken(ACCESS_TOKEN_EXP, this.makeUUID());
    }
    private String makeRefreshToken() {
        long REFRESH_TOKEN_EXP = System.currentTimeMillis() + ONE_DAY;
        return JwtToken.makeToken(REFRESH_TOKEN_EXP, this.makeTokenInfo());
    }

    private Map<String, Object> makeTokenInfo() {
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put(MEMBER_ID, memberId);
        tokenInfo.put(ROLE, role);
        return tokenInfo;
    }

    private Map<String, Object> makeUUID() {
        HashMap<String, Object> uuidInfo = new HashMap<>();
        uuidInfo.put(UUID_KEY, UUID.randomUUID());
        return uuidInfo;
    }

}
