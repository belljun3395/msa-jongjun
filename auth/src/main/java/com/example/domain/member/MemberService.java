package com.example.domain.member;

import com.example.web.dto.MemberJoinDTO;
import com.example.web.dto.MemberLoginDTO;
import com.example.web.dto.TokenDTO;



public interface MemberService {

    void join(MemberJoinDTO memberJoinDTO);

    TokenDTO login(MemberLoginDTO memberLoginDTO);

    void adjustRole(Member member, Role role);

    void logout(String accessTokenValue);

}
