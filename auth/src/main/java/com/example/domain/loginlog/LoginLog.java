package com.example.domain.loginlog;

import com.example.domain.member.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private String clientType;

    private String location;

    private Timestamp timestamp;

    @Builder
    public LoginLog(Long memberId, Role role, String clientType, String location) {
        this.memberId = memberId;
        this.role = role;
        this.clientType = clientType;
        this.location = location;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

}
