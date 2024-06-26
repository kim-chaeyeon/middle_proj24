package com.example.blog.domain.member.service;


import com.example.blog.DataNotFoundException;
import com.example.blog.domain.email.EmailService;
import com.example.blog.domain.member.dto.JoinRequest;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.domain.post.Post;
import com.example.blog.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final PostRepository postRepository;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private  final VerificationCodeService  verificationCodeService;

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
                         String email, String region, String favoriteFood, String mbti, String sns, MultipartFile thumbnail, String gender) {
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
        member.setGender(gender);
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




    // 이메일로 아이디 찾기
    public String findUsernameByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
            return member.getUsername();
        }
        throw new DataNotFoundException("해당 이메일로 등록된 사용자가 없습니다."); // 사용자가 없을 경우 예외 발생
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
    public List<Post> getPostsByNickname(String nickname) {
        return postRepository.findByAuthorNickname(nickname);
    }

    public void sendPasswordResetEmail(String username, String email) {
        // 사용자 확인
        Member member = memberRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 일치하지 않습니다."));

        // 인증코드 생성 및 이메일 전송
        String verificationCode = verificationCodeService.generateVerificationCode(email);
        String subject = "비밀번호 재설정 인증코드";
        String body = "비밀번호 재설정을 위한 인증코드: " + verificationCode;
        emailService.send(email, subject, body);

        // 회원 객체에 인증코드 저장
        member.setResetToken(verificationCode);
        memberRepository.save(member);
    }

    // 인증코드를 사용하여 비밀번호 재설정
    @Transactional
    public void resetPassword(String username, String email, String newPassword) {
        // 사용자 확인
        Member member = memberRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 일치하지 않습니다."));


        // 비밀번호 변경
        member.setPassword(passwordEncoder.encode(newPassword));
        member.setResetToken(null); // 인증코드 초기화
        memberRepository.save(member);
    }



}
