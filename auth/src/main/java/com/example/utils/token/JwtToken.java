package com.example.utils.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtToken {

    private static final String SIGN_KEY_VALUE = "iskeyhavetolongidontknowaboutthis";
    private static final Key SIGN_KEY = Keys.hmacShaKeyFor(SIGN_KEY_VALUE.getBytes(StandardCharsets.UTF_8));
    private static final String UUID_KEY = "uuid";
    private static String HEADER_TYPE = "JWT";
    private static String HEADER_ALG = "HS256";
    private static String TOKEN_ISSUER = "auth app";

    private static Long TEN_MINUTE = 10 * 60 * 1000L;


    public static String makeToken(Long expiryDate, Map<String, Object> claims) {

        Map<String, Object> jwtHeader = setHeader(HEADER_TYPE, HEADER_ALG);

        return Jwts.builder()
                .signWith(SIGN_KEY,SignatureAlgorithm.HS256)
                .setHeader(jwtHeader)
                .setClaims(claims)
                .setIssuer(TOKEN_ISSUER)
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

    public static boolean validateExpirationTime(String token) {
        Long expirationTime = getExpirationTime(token);
        long now = System.currentTimeMillis();
        if (expirationTime - now < TEN_MINUTE) {
            expirationRefresh(token, now);
            return true;
        }
        return false;
    }

    private static Long getExpirationTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGN_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    private static void expirationRefresh(String token, long now) {
        Jwts.parserBuilder()
                .setSigningKey(SIGN_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .setExpiration(new Date(now + TEN_MINUTE));
    }

    public static String getUUID(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(SIGN_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(UUID_KEY);
    }
}
