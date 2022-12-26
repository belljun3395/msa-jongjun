package com.example.web.controller;

import com.example.domain.member.MemberService;
import com.example.domain.member.Role;
import com.example.web.dto.*;
import com.example.web.response.ApiResponse;
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

    @GetMapping("/{memberId}")
    public ApiResponse<ApiResponse.withData> memberInfo(@PathVariable("memberId") Long memberId) {
        MemberInfoDTO memberInfoDTO = memberService.memberInfo(memberId);
        return ApiResponseGenerator.success(memberInfoDTO, HttpStatus.OK, 1100, "memberInfo");
    }

    @PostMapping("/join")
    public ApiResponse<ApiResponse.withCodeAndMessage> join(@Validated MemberJoinDTO memberJoinDTO) {
        memberService.join(memberJoinDTO);
        return ApiResponseGenerator.success(HttpStatus.OK, 1100, "join success");
    }

    @PostMapping("/login")
    public void login(@Validated MemberLoginDTO memberLoginDTO, HttpServletResponse response) {
        TokenDTO tokenDTO = memberService.login(memberLoginDTO);
        response.setHeader(AUTHORIZATION, tokenDTO.getAuthorizationToken());
        response.setHeader("Access-Control-Expose-Headers", AUTHORIZATION);
        response.addCookie(tokenDTO.getTokenCookie());
    }

    @PostMapping("/logout")
    public void logout(@CookieValue("refresh_token") String token, HttpServletResponse response) {
        memberService.logout(token);
        removeCookieToken(response);
    }

    private static void removeCookieToken(HttpServletResponse response) {
        Cookie refreshToken = new Cookie("refresh_token", null);
        refreshToken.setMaxAge(0);
        refreshToken.setPath("/");
        response.addCookie(refreshToken);
    }

    @PostMapping("/role")
    public void adjustRole(MemberRoleDTO memberRoleDTO) {
        memberService.adjustRole(memberRoleDTO.getMemberId(), Role.makeRole(memberRoleDTO.getRole()));
    }

    @PostMapping("/email")
    public String emailAuth(MemberAuthInfoDTO memberAuthInfoDTO) {
        return memberService.emailAuth(memberAuthInfoDTO);
    }

    @PostMapping("/email/key")
    public boolean validateKey(AuthKeyInfoDTO authKeyInfoDTO) {
        return memberService.validateAuthKey(authKeyInfoDTO);
    }
}
