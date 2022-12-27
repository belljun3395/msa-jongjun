package com.example.domain.token.accessToken;

import com.example.web.dto.MemberInfoDTO;

public interface AccessTokenService {

    void save(AccessToken accessToken);

    MemberInfoDTO browseMemberMatch(String accessToken);

    AccessToken findAccessToken(String accessTokenValue);

    AccessToken makeAccessToken(String refreshToken);

    boolean validateAccessTokenRole(String accessTokenValue, String role);

}
