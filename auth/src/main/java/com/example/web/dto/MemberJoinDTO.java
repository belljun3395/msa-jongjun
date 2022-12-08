package com.example.web.dto;

import com.example.domain.member.Member;
import com.example.domain.member.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class MemberJoinDTO {

    @Email
    private String email;

    @NotNull
    private String name;

    @NotNull
    private String password;


    public Member convertToMember() {
        return new Member(name, email, password, Role.makeRole("member"));
    }

}
