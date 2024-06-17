package com.example.blog.domain.member.service;

<<<<<<< HEAD
=======

>>>>>>> 38ad50beb563f6ee5f8947b6073eef57ddd9963c
import com.example.blog.domain.member.entity2.Verification;
import com.example.blog.domain.member.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationCodeService {

    private final VerificationRepository verificationRepository;

    public VerificationCodeService(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }


    public boolean verifyCode(String email, String verificationCode) {
        Optional<Verification> verificationOptional = verificationRepository.findByEmailAndVerificationCode(email, verificationCode);
        return verificationOptional.isPresent();
    }

    public String generateVerificationCode(String email) {

        String verificationCode = generateCode();

        saveVerificationCode(email, verificationCode);

        return verificationCode;
    }

//    private void saveVerificationCode(String email, String verificationCode) {
//        Verification entity = new Verification();
//        entity.setEmail(email);
//        entity.setVerificationCode(verificationCode);
//        verificationRepository.save(entity);
//    }

    private void saveVerificationCode(String email, String verificationCode) {
        Verification verifiation = Verification.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();
        verificationRepository.save(verifiation);
    }


    private String generateCode() {
        // 여기에 코드 생성 로직을 구현하세요
        // 예를 들어, 무작위 알파벳 숫자 코드를 생성할 수 있습니다
        return UUID.randomUUID().toString().substring(0, 6); // 6자리 무작위 코드 생성
    }

    // 다른 메서드
}
