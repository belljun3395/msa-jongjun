package com.example.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RedisHash(value = "email_auth_key")
public class AuthKey {

    @Id
    private String uuid;

    private String key;

    private Long memberId;

    @TimeToLive
    private Long expiredTime;

    public AuthKey(String uuid, String key, Long memberId) {
        this.uuid = uuid;
        this.key = key;
        this.memberId = memberId;
        this.expiredTime = 3 * 60L * 1000L;
    }

}
