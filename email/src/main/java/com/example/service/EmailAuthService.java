package com.example.service;

import com.example.dto.AuthKeyInfo;
import com.example.dto.EmailAuthInfo;

public interface EmailAuthService {
    String sendAuthEmail(EmailAuthInfo emailAuthInfo) throws Exception;

    boolean validateAuthKey(AuthKeyInfo authKeyInfo);
}
