package com.example.utils.token;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Getter
@Component
@PropertySource("classpath:token.properties")
public class TokenConfig {

    public static Long ONE_DAY;
    @Value("${token.oneDay}")
    public void setONE_DAY(Long ONE_DAY) {
        TokenConfig.ONE_DAY = ONE_DAY;
    }

    public static Long TWENTY_MIN;
    @Value("${token.twentyMin}")
    public void setTWENTY_MIN(Long TWENTY_MIN) {
        TokenConfig.TWENTY_MIN = TWENTY_MIN;
    }

    public static String UUID_KEY;
    @Value("${token.key.uuid}")
    public void setTWENTY_MIN(String UUID) {
        TokenConfig.UUID_KEY = UUID;
    }

    public static String MEMBERID_KEY;
    @Value("${token.key.memberId}")
    public void setMEMBERID_KEY(String MEMBERID_KEY) {
        TokenConfig.MEMBERID_KEY = MEMBERID_KEY;
    }

    public static String ROLE_KEY;
    @Value("${token.key.role}")
    public void setROLE_KEY_KEY(String ROLE_KEY) {
        TokenConfig.ROLE_KEY = ROLE_KEY;
    }
}
