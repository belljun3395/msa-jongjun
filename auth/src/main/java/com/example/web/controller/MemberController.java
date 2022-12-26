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

    private static final String MEMBER_ID = "memberId";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String HOME_PATH = "/";
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ApiResponse<ApiResponse.withData> memberInfo(@PathVariable(MEMBER_ID) Long memberId) {
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
        // todo 다른 도메인간 headers 전송 여부 공부
        response.setHeader(AUTHORIZATION_HEADER, tokenDTO.getAuthorizationToken());
        response.setHeader("Access-Control-Expose-Headers", AUTHORIZATION_HEADER);
        response.addCookie(tokenDTO.getTokenCookie());
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(REFRESH_TOKEN) String token, HttpServletResponse response) {
        memberService.logout(token);
        removeCookieToken(response);
    }

    private static void removeCookieToken(HttpServletResponse response) {
        Cookie refreshToken = new Cookie(REFRESH_TOKEN, null);
        refreshToken.setMaxAge(0);
        refreshToken.setPath(HOME_PATH);
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
