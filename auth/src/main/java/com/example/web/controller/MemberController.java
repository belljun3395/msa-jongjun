package com.example.web.controller;

import com.example.domain.member.MemberService;
import com.example.domain.member.Role;
import com.example.utils.token.JwtToken;
import com.example.utils.token.TokenConfig;
import com.example.web.dto.*;
import com.example.web.response.ApiResponse;
import com.example.web.response.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private static final String MEMBER_ID = "memberId";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String HOME_PATH = "/";
    private static final String ROLE_VALUE = "roleValue";
    private static final String COOKIE_NAME = "refresh_token";
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

    @PostMapping("/role/cookie")
    public void adjustCookie(@CookieValue String refresh_token, String roleValue, HttpServletResponse response) {
        String newRefreshToken = makeNewToken(refresh_token, roleValue);
        Cookie newCookie = makeNewCookie(newRefreshToken);
        response.addCookie(newCookie);
    }

    private static String makeNewToken(String refresh_token, String roleValue) {
        String memberId = JwtToken.decodeToken(refresh_token, TokenConfig.MEMBERID_KEY);
        Long expirationTime = JwtToken.getExpirationTime(refresh_token);

        Map<String, Object> newMemberInfo = new HashMap<>();
        newMemberInfo.put(TokenConfig.MEMBERID_KEY, memberId);
        newMemberInfo.put(TokenConfig.ROLE_KEY, Role.makeRole(roleValue));

        return JwtToken.makeToken(expirationTime, newMemberInfo);
    }

    private static Cookie makeNewCookie(String newRefreshToken) {
        Cookie cookie = new Cookie(COOKIE_NAME, newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(HOME_PATH);
        return cookie;
    }
}
