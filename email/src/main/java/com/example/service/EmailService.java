package com.example.service;

import javax.mail.internet.MimeMessage;

public interface EmailService {
    void sendEmail(MimeMessage message) throws Exception;
}
