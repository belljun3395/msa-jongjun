package com.example.domain.authMemberInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RedisHash(value = "accessToken")
public class AuthMemberInfo {
    @Id
    private String uuid;

    private String key;

    public AuthMemberInfo(String uuid, String key) {
        this.uuid = uuid;
        this.key = key;
    }
}
