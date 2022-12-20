package com.example.token;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "auth", fallbackFactory = FeignValidateAccessTokenFallbackFactory.class)
public interface FeignValidateAccessToken {

    @RequestMapping(path = "/tokens/members/validation")
    boolean validateAccessToken(@RequestHeader("Authorization") String token);

}
