package com.example.event;

import com.example.dto.AuthKeyInfo;
import com.example.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthKeyKafkaListener {

    private final EmailAuthService service;

    @KafkaListener(groupId = "auth", topics = "keyAuth", containerFactory = "authKeyInfoConcurrentKafkaListenerContainerFactory")
    public void validateAuthKey(AuthKeyInfo authKeyInfo) {
        service.validateAuthKey(authKeyInfo);
    }
}
