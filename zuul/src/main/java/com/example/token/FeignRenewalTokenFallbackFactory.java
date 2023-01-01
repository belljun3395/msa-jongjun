package com.example.token;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignRenewalTokenFallbackFactory implements FallbackFactory<FeignRenewalToken> {

    @Override
    public FeignRenewalToken create(Throwable cause) {
        log.error("error = [{}][{}]", cause.getCause(), cause.getMessage());
        return null;
    }
}
