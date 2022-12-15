package com.example.web.dto;

import lombok.Data;
import javax.servlet.http.Cookie;

@Data
public class TokenDTO {

    private String COOKIE_NAME = "access_token";
    private String COOKIE_PATH = "/";
    private String AUTH_TYPE = "Bearer";

    private String accessToken;

    private String refreshToken;

    public TokenDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Cookie getTokenCookie() {
        Cookie access_token = new Cookie(COOKIE_NAME, this.getBearerRefreshToken());
        access_token.setHttpOnly(true);
        access_token.setSecure(true);
        access_token.setPath(COOKIE_PATH);
        return access_token;
    }

    private String getBearerRefreshToken() {
        return AUTH_TYPE + getRefreshToken();
    }
}
