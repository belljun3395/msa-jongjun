package com.example.domain.redis;

import com.example.domain.member.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Getter
@NoArgsConstructor
@RedisHash(value = "accessToken")
public class AccessToken {

    @Id
    private String accessTokenValue;

    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @TimeToLive
    private Long expiredTime;

    // todo convert getBy accessToken -> value and expireTime and memberId
    public AccessToken(String accessTokenValue, Long memberId, Role role) {
        this.accessTokenValue = accessTokenValue;
        this.memberId = memberId;
        this.role = role;
        this.expiredTime = 1800L;
    }
}

