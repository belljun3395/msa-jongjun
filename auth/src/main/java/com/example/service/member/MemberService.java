package com.example.service.member;

import com.example.domain.member.Member;
import com.example.domain.member.Role;

public interface MemberService {

    void join(Member member);

    void adjustRole(Member member, Role role);

}
