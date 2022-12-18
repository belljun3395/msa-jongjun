package com.example.web.controller;

import com.example.domain.member.MemberService;
import com.example.web.dto.MemberLoginDTO;
import com.example.web.dto.TokenDTO;
import com.example.web.response.ApiResponse;
import com.example.web.dto.MemberJoinDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/members")
public class MemberController {

    private final String AUTHORIZATION = "Authorization";
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Null>> join(WebRequest request, @Validated MemberJoinDTO memberJoinDTO) {
        return new ResponseEntity<>(memberService.join(memberJoinDTO)
                .setPath(request),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public void login(@Validated MemberLoginDTO memberLoginDTO, HttpServletResponse response) {
        TokenDTO tokenDTO = memberService.login(memberLoginDTO);
        response.setHeader(AUTHORIZATION, tokenDTO.getAuthorizationToken());
        response.addCookie(tokenDTO.getTokenCookie());
    }
}
