package com.example.token;

import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "auth", fallbackFactory = FeignValidateAccessTokenFallbackFactory.class)
public interface FeignValidateAccessToken {

    @RequestMapping(path = "/tokens/validation")
    boolean validateAccessToken(@RequestHeader("Authorization") String token);

    @RequestMapping(path = "/tokens/validation/role/{role}")
    boolean validateAccessTokenRole(@RequestHeader("Authorization") String token, @PathVariable("role") String role);

}
