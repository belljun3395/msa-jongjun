package com.example.web.controller;

import com.example.domain.token.accessToken.AccessToken;
import com.example.domain.token.accessToken.AccessTokenService;
import com.example.web.dto.MemberInfoDTO;
import com.example.web.response.ApiResponse;
import com.example.web.response.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tokens")
public class TokenController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final AccessTokenService service;

    @GetMapping
    public ApiResponse<ApiResponse.withData> accessTokenInfo(@RequestHeader(AUTHORIZATION_HEADER) String access_token) {
        AccessToken accessToken = service.findAccessToken(access_token);
        return ApiResponseGenerator.success(accessToken, HttpStatus.OK, 1100, "member info");
    }

    @GetMapping("/access")
    public AccessToken makeAccessToken(@CookieValue String refresh_token) {
        return service.makeAccessToken(refresh_token);
    }

    @GetMapping("/members")
    public MemberInfoDTO browseMemberMatch(HttpServletResponse response, @RequestHeader(AUTHORIZATION_HEADER) String accessTokenValue) {
        response.setHeader(AUTHORIZATION_HEADER, accessTokenValue);
        return service.browseMemberMatch(accessTokenValue);
    }

    @GetMapping("/validation")
    public boolean validateAccessToken(@RequestHeader(AUTHORIZATION_HEADER) String accessTokenValue) {
        return service.validateAccessToken(accessTokenValue);
    }

    @GetMapping("/validation/role/{role}")
    public boolean validateAccessTokenRole(@RequestHeader(AUTHORIZATION_HEADER) String accessTokenValue, @PathVariable String role) {
        return service.validateAccessTokenRole(accessTokenValue, role);
    }
}
