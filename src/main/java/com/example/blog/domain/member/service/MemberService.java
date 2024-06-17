package com.example.blog.domain.member.service;


import com.example.blog.domain.member.dto.JoinRequest;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean checkLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    public void join(JoinRequest joinRequest) {
        memberRepository.save(joinRequest.toEntity());
    }

<<<<<<< HEAD
//    public Member login(LoginRequest loginRequest) {
//        Optional<Member> optionalMember = memberRepository.findByLoginId(loginRequest.getLoginId());
//
//        if (optionalMember.isPresent()) {
//            Member findMember = optionalMember.get();
//            if (passwordEncoder.matches(loginRequest.getPassword(), findMember.getPassword())) {
//                return findMember;
//            }
//        }
//        return null;
//    }
=======

>>>>>>> 38ad50beb563f6ee5f8947b6073eef57ddd9963c

    public Member getLoginMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    @Transactional
    public Member whenSocialLogin(String username, String nickname, String email) {
        Optional<Member> opMember = findByUsername(username);

        if (opMember.isPresent()) {
            return opMember.get();
        }

        // 새로운 회원 저장
        return signupSocialUser(username, nickname, email);
    }

    @Transactional
    public Member signupSocialUser(String username, String nickname, String email) {
        // 소셜 로그인한 회원 저장
        return signup(username, "", nickname, "",0, email, "", "", "", "", "",  null);
    }

    @Transactional
    public Member signup(String username, String phoneNumber, String nickname, String password, int age,
                         String email, String gender, String region, String favoriteFood, String mbti, String sns, MultipartFile thumbnail) {
        String thumbnailRelPath = "post/" + UUID.randomUUID().toString() + ".jpg";
        File thumbnailFile = new File(fileDirPath + "/" + thumbnailRelPath);

        try {
            thumbnail.transferTo(thumbnailFile);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }

        Member member = Member.builder()
                .username(username)
                .phoneNumber(phoneNumber)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .email(email)
                .gender(gender)
                .region(region)
                .favoriteFood(favoriteFood)
                .age(age)
                .sns(sns)
                .mbti(mbti)
                .thumbnailImg(thumbnailRelPath)
                .build();

        return memberRepository.save(member);
    }

    @Value("${custom.fileDirPath}")
    private String fileDirPath;

    @Transactional
    public Member modify(Member member, String phoneNumber, String nickname, String password, int age,
                         String email, String region, String favoriteFood, String mbti, String sns, MultipartFile thumbnail) {
        String thumbnailRelPath = "post/" + UUID.randomUUID().toString() + ".jpg";
        File thumbnailFile = new File(fileDirPath + "/" + thumbnailRelPath);

        try {
            thumbnail.transferTo(thumbnailFile);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        // 기존 회원 정보를 수정합니다.
        member.setPhoneNumber(phoneNumber);
        member.setNickname(nickname);
        member.setPassword(passwordEncoder.encode(password));
        member.setEmail(email);
        member.setRegion(region);
        member.setFavoriteFood(favoriteFood);
        member.setAge(age);
        member.setSns(sns);
        member.setMbti(mbti);
        member.setThumbnailImg(thumbnailRelPath);

        // 수정된 회원 정보를 저장하고 반환합니다.
        return memberRepository.save(member);
    }


    private Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Member getCurrentMember() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    }

    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElse(null);
    }

}
