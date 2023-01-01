package com.example.utils.token;

import java.util.Map;

public interface TokenProvider {

    String getToken(Long expireTime, Map<String, Object> claims);

}
