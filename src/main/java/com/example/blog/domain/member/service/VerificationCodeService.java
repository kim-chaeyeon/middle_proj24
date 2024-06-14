package com.example.blog.domain.member.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VerificationCodeService {


    private static final int VERIFICATION_CODE_LENGTH = 6;

    public String generateVerificationCode() {

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // 인증 코드 확인
    public boolean verifyVerificationCode(String verificationCode) {

        return verificationCode.equals(generateVerificationCode());
    }
}
