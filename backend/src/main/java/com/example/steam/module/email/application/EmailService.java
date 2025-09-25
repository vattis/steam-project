package com.example.steam.module.email.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    //필요한 설정은 Config에 미리 설정해두고 보내는 이, 제목, 컨텐츠만 따로 받아서 MimeMessageHelper로 조립 후 바로 전송
    public void sendEmail(String toEmail, String title, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(toEmail); //보내는 이
        mimeMessageHelper.setSubject(title); //제목
        mimeMessageHelper.setText(content, true); //내용
        mimeMessageHelper.setReplyTo("noreply@steam.com"); //답신 받을 메일(없는 메일로 설정)
        try{
            mailSender.send(mimeMessage);
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}