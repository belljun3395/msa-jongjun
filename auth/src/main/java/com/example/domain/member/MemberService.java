package com.example.domain.member;

import com.example.web.dto.*;


public interface MemberService {

    void join(MemberJoinDTO memberJoinDTO);

    TokenDTO login(MemberLoginDTO memberLoginDTO);

    void adjustRole(Long member, Role role);

    void logout(String accessTokenValue);

    String emailAuth(MemberAuthInfoDTO memberAuthInfoDTO);

    boolean validateAuthKey(AuthKeyInfoDTO authKeyInfoDTO);
}
