package com.example.blog.domain.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private  final JavaMailSender mailSender;

    public void send(String to, String subject, String body) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to); // 메일 수신자
            mimeMessageHelper.setSubject(subject); // 메일 제목
            mimeMessageHelper.setText(body, true); // 메일 본문 내용 , HTML 여부
            mailSender.send(mimeMessage); // 메일 발송

        } catch(MessagingException e) {
            throw new RuntimeException(e);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendVerificationCode(String to, String subject, String body, String verificationCode) {
        String bodyWithCode = body + "\n인증 코드: " + verificationCode;
        send(to, subject, bodyWithCode);
    }

}
