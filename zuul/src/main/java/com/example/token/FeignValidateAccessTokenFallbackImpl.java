package com.example.token;

import org.springframework.stereotype.Component;

@Component
public class FeignValidateAccessTokenFallbackImpl implements FeignValidateAccessToken {
    @Override
    public boolean validateAccessToken(String token) {
        return false;
    }
}
