package com.example.web.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class MemberLoginDTO {

    @Email
    private String email;

    @NotNull
    private String password;

    private String clientType;

    private String location;

    public MemberLoginDTO(String email, String password, String clientType, String location) {
        this.email = email;
        this.password = password;
        this.clientType = clientType;
        this.location = location;
    }
}
