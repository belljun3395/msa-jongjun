package com.example.utils.token.jwt;

import com.example.utils.token.TokenConsumer;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtTokenConsumer implements TokenConsumer {

    private final JwtTokenConfig jwtTokenConfig;


    @Override
    public String getPayloadClaim(String token, String key) {
        return String.valueOf(Jwts.parserBuilder()
                .setSigningKey(jwtTokenConfig.getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(key));
    }

    @Override
    public Long getExpirationTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtTokenConfig.getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }
}
