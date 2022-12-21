package com.example.web.controller;

import com.example.domain.token.accessToken.AccessTokenService;
import com.example.web.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/tokens")
public class TokenController {

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final AccessTokenService service;

    @GetMapping("/members")
    public MemberInfoDTO browseMemberMatch(HttpServletResponse response, @RequestHeader("Authorization") String accessTokenValue) {
        response.setHeader(AUTHORIZATION_HEADER, accessTokenValue);
        return service.browseMemberMatch(accessTokenValue);
    }

    @GetMapping("/validation")
    public boolean validateAccessToken(@RequestHeader("Authorization") String accessTokenValue) {
        return service.validateAccessToken(accessTokenValue);
    }

    @GetMapping("/validation/role/{role}")
    public boolean validateAccessTokenRole(@RequestHeader("Authorization") String accessTokenValue, @PathVariable String role) {
        return service.validateAccessTokenRole(accessTokenValue, role);
    }
}
