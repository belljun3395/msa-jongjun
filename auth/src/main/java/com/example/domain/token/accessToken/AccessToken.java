package com.example.domain.token.accessToken;

import com.example.domain.member.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RedisHash(value = "accessToken")
public class AccessToken {

    private final Long REDIS_EXPIRED_TIME = 1800L;

    @Id
    private String accessTokenValue;

    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @TimeToLive
    private Long expiredTime;

    public AccessToken(String accessTokenValue, Long memberId, Role role) {
        this.accessTokenValue = accessTokenValue;
        this.memberId = memberId;
        this.role = role;
        this.expiredTime = REDIS_EXPIRED_TIME;
    }
}

