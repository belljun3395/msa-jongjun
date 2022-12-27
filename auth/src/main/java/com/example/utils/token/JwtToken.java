package com.example.utils.token;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.lettuce.core.RedisException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtToken {

    public static String makeToken(Long expiryDate, Map<String, Object> claims) {

        Map<String, Object> jwtHeader = setHeader(JwtTokenConfig.HEADER_TYPE, JwtTokenConfig.HEADER_ALG);

        return Jwts.builder()
                .signWith(JwtTokenConfig.getSIGN_KEY(),SignatureAlgorithm.HS256)
                .setHeader(jwtHeader)
                .setClaims(claims)
                .setIssuer(JwtTokenConfig.TOKEN_ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiryDate))
                .compact();
    }

    private static Map<String, Object> setHeader(String typ, String alg) {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("typ", typ);
        jwtHeader.put("alg", alg);
        return jwtHeader;
    }

    public static boolean validateToken(String tokenValue) {
        try {
            validateJwts(tokenValue);
            return true;
        } catch (JwtException | RedisException e) {
            return false;
        }
    }

    private static void validateJwts(String token) {
        Jwts.parserBuilder()
                .setSigningKey(JwtTokenConfig.getSIGN_KEY())
                .build()
                .parseClaimsJws(token);
    }

    public static boolean refreshExpirationTime(String token) {
        Long expirationTime = getExpirationTime(token);
        Long issuedAtTime = getIssuedAtTime(token);
        if (isAccessToken(expirationTime, issuedAtTime)) {
            if (refreshTokenExpirationTime(token, expirationTime, TokenConfig.TWENTY_MIN)) {
                return true;
            }
            return false;
        }
        if (refreshTokenExpirationTime(token, expirationTime, TokenConfig.ONE_DAY)) {
            return true;
        }
        return false;
    }

    private static boolean isAccessToken(Long expirationTime, Long issuedAtTime) {
        return (expirationTime - issuedAtTime) == TokenConfig.TWENTY_MIN;
    }

    private static boolean refreshTokenExpirationTime(String token, Long expirationTime, Long compareTime) {
        long now = System.currentTimeMillis();
        if (expirationTime - now < compareTime) {
            expirationRefresh(token, now);
            return true;
        }
        return false;
    }

    private static Long getExpirationTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(JwtTokenConfig.getSIGN_KEY())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    private static Long getIssuedAtTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(JwtTokenConfig.getSIGN_KEY())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getIssuedAt()
                .getTime();
    }

    private static void expirationRefresh(String token, long now) {
        Jwts.parserBuilder()
                .setSigningKey(JwtTokenConfig.getSIGN_KEY())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .setExpiration(new Date(now + TokenConfig.ONE_DAY));
    }

    public static String getUUID(String token) {
        return String.valueOf(Jwts.parserBuilder()
                .setSigningKey(JwtTokenConfig.getSIGN_KEY())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(TokenConfig.UUID_KEY));
    }

    public static String decodeToken(String token, String key) {
        return String.valueOf(Jwts.parserBuilder()
                .setSigningKey(JwtTokenConfig.getSIGN_KEY())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(key));
    }
}
