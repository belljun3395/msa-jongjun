package com.example.service;

import com.example.dto.AuthKeyInfo;
import com.example.dto.EmailAuthInfo;

public interface EmailAuthService {
    void sendAuthEmail(EmailAuthInfo emailAuthInfo) throws Exception;

}
