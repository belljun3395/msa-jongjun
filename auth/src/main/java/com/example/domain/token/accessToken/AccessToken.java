package com.example.domain.token.accessToken;

import com.example.domain.member.Role;
import com.example.utils.token.JwtToken;
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
public class AccessToken  {

    @Id
    private String uuid;

    private String accessTokenValue;

    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @TimeToLive
    private Long expiredTime;

    public AccessToken(String accessTokenValue, Long memberId, Role role) {
        this.uuid = JwtToken.getUUID(accessTokenValue);
        this.accessTokenValue = accessTokenValue;
        this.memberId = memberId;
        this.role = role;
        this.expiredTime = 20 * 60L * 1000L;
    }

    public void refreshExpiredTime() {
        this.expiredTime += 10 * 60L * 1000L;
    }
}

