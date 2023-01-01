package com.example.utils.token.jwt;

import com.example.utils.token.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private final JwtTokenConfig jwtTokenConfig;

    @Override
    public String getToken(Long expireTime, Map<String, Object> claims) {
        Map<String, Object> jwtHeader = setHeader(jwtTokenConfig.getType(), jwtTokenConfig.getAlg());

        return Jwts.builder()
                .signWith(jwtTokenConfig.getSignKey(), SignatureAlgorithm.HS256)
                .setHeader(jwtHeader)
                .setClaims(claims)
                .setIssuer(jwtTokenConfig.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(expireTime))
                .compact();
    }

    private Map<String, Object> setHeader(String typ, String alg) {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("typ", typ);
        jwtHeader.put("alg", alg);
        return jwtHeader;
    }
}
