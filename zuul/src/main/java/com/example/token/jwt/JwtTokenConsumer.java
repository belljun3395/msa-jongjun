package com.example.token.jwt;

import com.example.exception.NotValidateTokenExceptionCustom;
import com.example.token.TokenConsumer;
import io.jsonwebtoken.ExpiredJwtException;
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

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtTokenConfig.getSignKey())
                    .build();
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            return false;
        } catch (Exception exception) {
            throw new NotValidateTokenExceptionCustom();
        }
    }
}
