package com.example.utils.token;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Getter
@Component
public class TokenConfig {
    @Value("${token.oneDay}")
    private Long oneDay;

    @Value("${token.twentyMin}")
    private Long twentyMin;

    @Value("${token.key.memberId}")
    private String memberIdKey;

    @Value("${token.key.role}")
    private String roleKey;
}
