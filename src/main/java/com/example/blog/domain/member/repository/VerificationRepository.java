package com.example.blog.domain.member.repository;


import com.example.blog.domain.member.entity2.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public  interface VerificationRepository extends JpaRepository<Verification, String> {

    Optional<Verification> findByEmailAndVerificationCode(String email, String verificationCode);
}
