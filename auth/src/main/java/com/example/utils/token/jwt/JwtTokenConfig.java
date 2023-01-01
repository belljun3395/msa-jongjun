package com.example.utils.token.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


@Getter
@Component
public class JwtTokenConfig {

    @Value("${jwt.type}")
    private String type;

    @Value("${jwt.signKey}")
    private String signKey;

    @Value("${jwt.header.alg}")
    private String alg;

    @Value("${jwt.issuer}")
    private String issuer;

    public SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(signKey
                .getBytes(StandardCharsets.UTF_8));
    }
}
