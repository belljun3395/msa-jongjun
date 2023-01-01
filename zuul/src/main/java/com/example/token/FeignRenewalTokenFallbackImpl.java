package com.example.token;

import org.springframework.stereotype.Component;

@Component
public class FeignRenewalTokenFallbackImpl implements FeignRenewalToken {
    @Override
    public String getNewToken() {
        return null;
    }
}
