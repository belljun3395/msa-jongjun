package com.example.domain.member;

import com.example.domain.member.Member;
import com.example.domain.member.Role;
import com.example.web.dto.MemberJoinDTO;
import com.example.web.dto.MemberLoginDTO;
import com.example.web.dto.TokenDTO;
import com.example.web.response.ApiResponse;

import javax.validation.constraints.Null;


public interface MemberService {

    ApiResponse<Null> join(MemberJoinDTO memberJoinDTO);

    TokenDTO login(MemberLoginDTO memberLoginDTO);

    void adjustRole(Member member, Role role);

    void logout(String accessTokenValue);

}
