package com.example.service;

import com.example.dto.EmailAuthInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService {

    private final EmailService emailService;
    private final JavaMailSender emailSender;

    @Value("${AdminMail.id}")
    private String adminMailId;


    @Override
    @Transactional
    public void sendAuthEmail(EmailAuthInfo emailAuthInfo) throws Exception {
        String ePw = emailAuthInfo.getKey();
        MimeMessage message = createMessage(emailAuthInfo.getEmail(), ePw);
        emailService.sendEmail(message);
    }

    private MimeMessage createMessage(String toEmail, String ePw)throws Exception{
        System.out.println("보내는 메일 : "+ toEmail);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, toEmail);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요! </h1>";
        msgg+= "<br>";
        msgg+= "<p>인증 키는 아래와 같습니다.<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>권한 변경 인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= ePw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress(adminMailId,"smilegateWinterDev"));//보내는 사람

        return message;
    }
}
