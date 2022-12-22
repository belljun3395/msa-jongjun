package com.example.event;

import com.example.dto.EmailAuthInfo;
import com.example.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAuthKafkaListener {

    private final EmailAuthService service;

    @KafkaListener(groupId = "auth", topics = "emailAuth", containerFactory = "emailAuthInfoConcurrentKafkaListenerContainerFactory")
    public void sendAuthEmail(EmailAuthInfo emailAuthInfo) throws Exception {
        service.sendAuthEmail(emailAuthInfo);
    }
}
