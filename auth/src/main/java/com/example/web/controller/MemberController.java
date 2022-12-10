package com.example.web.controller;

import com.example.service.member.MemberService;
import com.example.web.response.Response;
import com.example.web.dto.MemberJoinDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/members")
public class MemberController {

    private final String SUCCESS_JOIN = "success join";

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<Response<Object>> join(@Validated MemberJoinDTO memberJoinDTO) {

        memberService.join(memberJoinDTO);

        Response<Object> success_join = makeResponse(HttpStatus.CREATED.value(), SUCCESS_JOIN);

        return new ResponseEntity<Response<Object>>(success_join, HttpStatus.OK);
    }


    private static Response<Object> makeResponse(Integer status, String message) {
        Response<Object> success_join = Response.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .status(status)
                .message(message)
                .build();
        return success_join;
    }
}
