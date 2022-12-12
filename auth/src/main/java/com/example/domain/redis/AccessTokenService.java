package com.example.domain.redis;

import com.example.web.dto.MemberInfoDTO;

public interface AccessTokenService {

    void save(AccessToken accessToken);

    MemberInfoDTO browseMatchAccessToken(String accessToken);

}
