package com.example.token;

public interface TokenConsumer {

    String getPayloadClaim(String token, String key);

    Long getExpirationTime(String token);

    boolean validateToken(String token);

}
