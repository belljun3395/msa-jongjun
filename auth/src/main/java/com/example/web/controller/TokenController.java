package com.example.web.controller;

import com.example.domain.token.accessToken.AccessTokenService;
import com.example.web.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/tokens")
public class TokenController {

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final AccessTokenService service;

    @GetMapping("/members")
    public MemberInfoDTO browseMatchAccessToken(WebRequest request, HttpServletResponse response,
                                                @RequestHeader("Authorization") String accessTokenValue) {

        response.setHeader(AUTHORIZATION_HEADER, accessTokenValue);
        return service.browseMatchAccessToken(accessTokenValue);
    }

    @GetMapping("/members/validation")
    public boolean validateAccessToken(WebRequest request) {
        String accessTokenValue = request.getHeader(AUTHORIZATION_HEADER);
        service.validateAccessToken(accessTokenValue);
        return true;
    }
}
