package com.example.token;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignValidateAccessTokenFallbackFactory implements FallbackFactory<FeignValidateAccessToken> {

    @Override
    public FeignValidateAccessToken create(Throwable cause) {
        log.error("error = [{}][{}]", cause.getCause(), cause.getMessage());
        return token -> false;
    }
}
