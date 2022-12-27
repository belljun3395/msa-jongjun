package com.example.utils.token;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


@Getter
@Component
@PropertySource("classpath:jwt.properties")
public class JwtTokenConfig {

    public static String HEADER_TYPE;
    @Value("${jwt.type}")
    public void setHeaderType(String headerType) {
        JwtTokenConfig.HEADER_TYPE = headerType;
    }

    public static String SIGN_KEY_VALUE;
    @Value("${jwt.SIGN_KEY_VALUE}")
    public void setSIGN_KEY_VALUE(String SIGN_KEY_VALUE) {
        JwtTokenConfig.SIGN_KEY_VALUE = SIGN_KEY_VALUE;
    }

    public static SecretKey getSIGN_KEY() {
        return Keys.hmacShaKeyFor(JwtTokenConfig.SIGN_KEY_VALUE.getBytes(StandardCharsets.UTF_8));
    }

    public static String HEADER_ALG;
    @Value("${jwt.HEADER_ALG}")
    public void setHEADER_ALG(String HEADER_ALG) {
        JwtTokenConfig.HEADER_ALG = HEADER_ALG;
    }

    public static String TOKEN_ISSUER;
    @Value("${jwt.TOKEN_ISSUER}")
    public void setTOKEN_ISSUER(String TOKEN_ISSUER) {
        JwtTokenConfig.TOKEN_ISSUER = TOKEN_ISSUER;
    }
}
