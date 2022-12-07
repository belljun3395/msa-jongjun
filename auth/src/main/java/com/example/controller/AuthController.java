package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auths")
public class AuthController {

    //zuul application.yml의 zuul.routes.auth.path: /auths/** 의 **를 통해 auths 이하를 모두 캐시하여 eureka의 auth에 해당되는 url에 전달한다.
    @GetMapping(path = "/example1/{authParam}")
    public String authExample1(@PathVariable String authParam) {
        String auth = authParam;
        return "AuthApplication authExample1 method authParam : " + auth;
    }

    @GetMapping(path = "/example2")
    public String authExample2() {
        return "AuthApplication authExample2 method";
    }
}
