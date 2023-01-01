package com.example.token;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "auth", fallbackFactory = FeignRenewalTokenFallbackFactory.class)
public interface FeignRenewalToken {

    @RequestMapping(path = "/members/token/renewal")
    String getNewToken();

}
