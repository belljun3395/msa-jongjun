package com.example.utils.token;

public interface TokenConsumer {

    String getPayloadClaim(String token, String key);

    Long getExpirationTime(String token);

}
