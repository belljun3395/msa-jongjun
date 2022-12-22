package com.example.web.dto;

import lombok.Data;

@Data
public class MemberAuthInfoDTO {
    private Long memberId;
    private String email;
    private String uuid;
}
