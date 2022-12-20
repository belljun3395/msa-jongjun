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
    public MemberInfoDTO browseMatchAccessToken(HttpServletResponse response, @RequestHeader("Authorization") String accessTokenValue) {
        response.setHeader(AUTHORIZATION_HEADER, accessTokenValue);
        return service.browseMatchAccessToken(accessTokenValue);
    }

    @GetMapping("/members/validation")
    public boolean validateAccessToken(@RequestHeader("Authorization") String accessTokenValue) {
        service.validateAccessToken(accessTokenValue);
        return true;
    }
}
