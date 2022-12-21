package com.example.web.controller;

import com.example.domain.member.MemberService;
import com.example.web.dto.MemberLoginDTO;
import com.example.web.dto.TokenDTO;
import com.example.web.response.ApiResponse;
import com.example.web.dto.MemberJoinDTO;
import com.example.web.response.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final String AUTHORIZATION = "Authorization";
    private final MemberService memberService;

    @PostMapping("/join")
    public ApiResponse<ApiResponse.withCodeAndMessage> join(@Validated MemberJoinDTO memberJoinDTO) {
        memberService.join(memberJoinDTO);
        return ApiResponseGenerator.success(HttpStatus.OK, 1100, "join success");
    }

    @PostMapping("/login")
    public void login(@Validated MemberLoginDTO memberLoginDTO, HttpServletResponse response) {
        TokenDTO tokenDTO = memberService.login(memberLoginDTO);
        response.setHeader(AUTHORIZATION, tokenDTO.getAuthorizationToken());
        response.addCookie(tokenDTO.getTokenCookie());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        memberService.logout(token);
        removeCookieToken(response);
    }

    private static void removeCookieToken(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("refresh_token", null);
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);
//        response.setHeader("Set-Cookie", null);
    }
}
