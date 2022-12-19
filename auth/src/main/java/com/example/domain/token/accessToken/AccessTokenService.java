package com.example.domain.token.accessToken;

import com.example.web.dto.MemberInfoDTO;
import com.example.web.response.ApiResponse;

public interface AccessTokenService {

    void save(AccessToken accessToken);

    ApiResponse<MemberInfoDTO> browseMatchAccessToken(String accessToken);

}
