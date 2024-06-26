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

    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "비밀번호 재설정 안내";
        String body = "비밀번호를 재설정하려면 아래 링크를 클릭하세요:<br><br>" +
                "<a href=\"" + resetLink + "\">비밀번호 재설정</a><br><br>" +
                "이 링크는 1시간 동안 유효합니다.";
        send(to, subject, body);
    }
}
