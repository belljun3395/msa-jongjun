package com.example.domain.token.accessToken;

import com.example.web.dto.MemberInfoDTO;

public interface AccessTokenService {

    void save(AccessToken accessToken);

    MemberInfoDTO browseMatchAccessToken(String accessToken);

}
