package com.example.web.dto;

import lombok.Data;
import javax.servlet.http.Cookie;

@Data
public class TokenDTO {

    private String COOKIE_NAME = "refresh_token";
    private String COOKIE_PATH = "/";
    private String AUTH_TYPE = "Bearer";

    private String accessToken;

    private String refreshToken;

    public TokenDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Cookie getTokenCookie() {
        Cookie cookie = new Cookie(COOKIE_NAME, this.refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(COOKIE_PATH);
        return cookie;
    }

    public String getAuthorizationToken() {
        return AUTH_TYPE + " " + accessToken;
    }
}
