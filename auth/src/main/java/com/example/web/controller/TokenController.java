package com.example.web.controller;

import com.example.domain.token.accessToken.AccessTokenService;
import com.example.web.dto.MemberInfoDTO;
import com.example.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/tokens")
public class TokenController {

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final AccessTokenService service;

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<MemberInfoDTO>> browseMatchAccessToken(WebRequest request) {
        String accessTokenValue = request.getHeader(AUTHORIZATION_HEADER);
        return new ResponseEntity<>(service.browseMatchAccessToken(accessTokenValue)
                .setPath(request),
                HttpStatus.OK);
    }

    @GetMapping("/members/validation")
    public boolean validateAccessToken(WebRequest request) {
        String accessTokenValue = request.getHeader(AUTHORIZATION_HEADER);
        service.validateAccessToken(accessTokenValue);
        return true;
    }
}
