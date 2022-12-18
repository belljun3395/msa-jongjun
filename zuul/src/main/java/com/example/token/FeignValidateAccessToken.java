package com.example.token;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "auth")
public interface FeignValidateAccessToken {

    // todo change pathVariable to header
    @RequestMapping(path = "/tokens/member/{tokenValue}")
    boolean validateAccessToken(@PathVariable("tokenValue") String token);

}
