package com.example.web.dto;

import com.example.domain.member.Member;
import lombok.Data;

@Data
public class MemberInfoDTO {

    private String email;

    private String name;

    public MemberInfoDTO(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static MemberInfoDTO convertFrom(Member member) {
        return new MemberInfoDTO(member.getEmail(), member.getName());
    }

}
