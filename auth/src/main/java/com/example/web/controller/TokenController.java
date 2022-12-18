package com.example.web.controller;

import com.example.domain.token.accessToken.AccessToken;
import com.example.domain.token.accessToken.AccessTokenRepository;
import com.example.domain.token.accessToken.AccessTokenService;
import com.example.utils.token.JWTToken;
import com.example.web.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/tokens")
public class TokenController {
    private final AccessTokenService service;

    // todo change pathVariable to header
    @GetMapping("/member/{tokenValue}")
    public String browseMatchAccessToken(@PathVariable("tokenValue") String accessTokenValue) {
        MemberInfoDTO memberInfoDTO = service.browseMatchAccessToken(accessTokenValue);
        return "test";
    }
}
