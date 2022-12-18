package com.example.utils.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JWTToken {

    private static final String KEY = "iskeyhavetolongidontknowaboutthis";
    private static final Key key = Keys.hmacShaKeyFor(KEY.getBytes(StandardCharsets.UTF_8));
    private static String HEADER_TYPE = "JWT";
    private static String HEADER_ALG = "HS256";
    private static String TOKEN_ISSUER = "auth app";

    private static Long TEN_MINUTE = 10 * 60 * 1000L;

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

        // todo check
        Claims claims = Jwts.claims();
        Set<Map.Entry<String, Object>> entries = claim.entrySet();
        for (Map.Entry<String, Object> me : entries) {
            claims.put(me.getKey(), me.getValue());
        }

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setHeader(jwtHeader)
                .setClaims(claims)
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
    public static boolean checkRefresh(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        long now = System.currentTimeMillis();
        if (expiration.getTime() - now < TEN_MINUTE) {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .setExpiration(new Date(now + TEN_MINUTE));
            return true;
        }
        return false;
    }
}
