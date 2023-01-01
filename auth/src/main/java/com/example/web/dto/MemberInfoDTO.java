package com.example.web.dto;

import com.example.domain.member.Member;
import com.example.domain.member.Role;
import lombok.Data;

@Data
public class MemberInfoDTO {

    private Long memberId;

    private String email;

    private String name;

    private Role role;

    public MemberInfoDTO(Long memberId, String email, String name, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public static MemberInfoDTO convertFrom(Member member) {
        return new MemberInfoDTO(member.getId(), member.getEmail(), member.getName(), member.getRole());
    }

}
