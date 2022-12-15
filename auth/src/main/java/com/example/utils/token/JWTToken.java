package com.example.utils.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTToken {

    private static final String KEY = "iskeyhavetolongidontknowaboutthis";
    private static final Key key = Keys.hmacShaKeyFor(KEY.getBytes(StandardCharsets.UTF_8));
    private static String HEADER_TYPE = "JWT";
    private static String HEADER_ALG = "HS256";
    private static String TOKEN_ISSUER = "auth app";

    public static String makeToken(Date expiryDate) {

        Map<String, Object> jwtHeader = setHeader("JWT", "HS256");

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setHeader(jwtHeader)
                .setIssuer("auth app")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();

    }

    public static String makeToken(Date expiryDate, Map<String, Object> claim) {

        Map<String, Object> jwtHeader = setHeader(HEADER_TYPE, HEADER_ALG);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setHeader(jwtHeader)
                .setClaims(claim)
                .setIssuer(TOKEN_ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .compact();

    }

    private static Map<String, Object> setHeader(String typ, String alg) {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("typ", typ);
        jwtHeader.put("alg", alg);
        return jwtHeader;
    }
}
