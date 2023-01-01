package com.example.web.controller;

import com.example.domain.member.MemberService;
import com.example.domain.member.Role;
import com.example.utils.token.TokenConfig;
import com.example.utils.token.TokenConsumer;
import com.example.utils.token.TokenProvider;
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
    private static final String COOKIE_NAME = "refresh_token";
    private static final String HOME_PATH = "/";

    private final TokenConsumer tokenConsumer;
    private final TokenProvider tokenProvider;
    private final TokenConfig tokenConfig;
    private final MemberService memberService;

    @GetMapping
    public ApiResponse<ApiResponse.withData> memberInfo(@RequestHeader("Authorization") String token) {
        Long memberId = Long.valueOf(tokenConsumer.getPayloadClaim(token, tokenConfig.getMemberIdKey()));
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
        response.setHeader("Authorization", tokenDTO.getAuthorizationToken());
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.addCookie(tokenDTO.getTokenCookie());
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(COOKIE_NAME) String token, HttpServletResponse response) {
        memberService.logout(token);
        removeCookieToken(response);
    }

    private void removeCookieToken(HttpServletResponse response) {
        Cookie refreshToken = new Cookie(COOKIE_NAME, null);
        refreshToken.setMaxAge(0);
        refreshToken.setPath(HOME_PATH);
        response.addCookie(refreshToken);
    }

    @GetMapping("/token/renewal")
    public String renewalToken(@CookieValue(COOKIE_NAME) String token, HttpServletResponse response) {
        Long memberId = Long.valueOf(tokenConsumer.getPayloadClaim(token, tokenConfig.getMemberIdKey()));
        Role role = Role.makeRole(tokenConsumer.getPayloadClaim(token, tokenConfig.getRoleKey()));
        Map<String, Object> memberClaims = getMemberClaims(memberId, role);
        
        long now = System.currentTimeMillis();
        return tokenProvider.getToken(now + tokenConfig.getTwentyMin(), memberClaims);
    }

    private Map<String, Object> getMemberClaims(Long memberId, Role role) {
        Map<String, Object> memberClaims = new HashMap<>();
        memberClaims.put(tokenConfig.getMemberIdKey(), memberId);
        memberClaims.put(tokenConfig.getRoleKey(), role);
        return memberClaims;
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
        String newRefreshToken = makeToken(refresh_token, roleValue);
        Cookie newCookie = makeCookieToken(newRefreshToken);
        response.addCookie(newCookie);
    }

    private String makeToken(String refresh_token, String roleValue) {
        String memberId = tokenConsumer.getPayloadClaim(refresh_token, tokenConfig.getMemberIdKey());
        Long expirationTime = tokenConsumer.getExpirationTime(refresh_token);

        Map<String, Object> newMemberInfo = makeNewMemberInfo(roleValue, memberId);

        return tokenProvider.getToken(expirationTime, newMemberInfo);
    }

    private Map<String, Object> makeNewMemberInfo(String roleValue, String memberId) {
        Map<String, Object> newMemberInfo = new HashMap<>();
        newMemberInfo.put(tokenConfig.getMemberIdKey(), memberId);
        newMemberInfo.put(tokenConfig.getRoleKey(), Role.makeRole(roleValue));
        return newMemberInfo;
    }

    private Cookie makeCookieToken(String newRefreshToken) {
        Cookie cookie = new Cookie(COOKIE_NAME, newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(HOME_PATH);
        return cookie;
    }
}
