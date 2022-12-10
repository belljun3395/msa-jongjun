package com.example.service.member;

import com.example.domain.member.Member;
import com.example.domain.member.Role;
import com.example.web.dto.MemberJoinDTO;

public interface MemberService {

    void join(MemberJoinDTO memberJoinDTO);

    void adjustRole(Member member, Role role);

}
