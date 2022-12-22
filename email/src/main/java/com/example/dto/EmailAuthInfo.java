package com.example.dto;


import lombok.Data;


@Data
public class EmailAuthInfo {
    private String uuid;
    private Long memberId;
    private String email;
}
