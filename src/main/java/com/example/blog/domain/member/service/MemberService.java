package com.example.blog.domain.member.service;


import com.example.blog.domain.member.dto.JoinRequest;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
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
import java.util.List;
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
        return signup(username, "", nickname, "",0, email, "", "", "", "", "",  null, MemberRole.USER);
    }

    @Transactional
    public Member signup(String username, String phoneNumber, String nickname, String password, int age,
                         String email, String gender, String region, String favoriteFood, String mbti, String sns, MultipartFile thumbnail, MemberRole role) {
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
                .role(role)
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

    // 썸네일 저장
    private String saveThumbnail(MultipartFile thumbnail) {
        String thumbnailRelPath = "post/" + UUID.randomUUID().toString() + ".jpg";
        File thumbnailFile = new File(fileDirPath + "/" + thumbnailRelPath);

        try {
            thumbnail.transferTo(thumbnailFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return thumbnailRelPath;
    }

//    public boolean isAdmin(Member member) {
//        return member.isAdmin();
//    }

    // 모든 회원 정보 가져오기
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }


    public boolean isAdmin(Member member) {
        return member.getUsername().equals("admin");
    }

    @Transactional
    public void deleteMemberByAdmin(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. ID: " + username));
        deleteRelatedFiles(member.getThumbnailImg());
        memberRepository.delete(member);
    }

    //회원 탈퇴
    @Transactional
    public void deleteMember(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. ID: " + username));


        deleteRelatedFiles(member.getThumbnailImg());


        memberRepository.delete(member);
    }

    private void deleteRelatedFiles(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(fileDirPath + "/" + filePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("파일 삭제 성공: " + filePath);
                } else {
                    System.out.println("파일 삭제 실패: " + filePath);
                }
            }
        }
    }
}