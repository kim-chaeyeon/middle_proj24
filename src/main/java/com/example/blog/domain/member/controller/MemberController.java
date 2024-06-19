package com.example.blog.domain.member.controller;


import com.example.blog.domain.block.service.BlockService;
import com.example.blog.domain.email.EmailService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.member.service.VerificationCodeService;


import com.example.blog.domain.report.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final EmailService emailService;
    private final MemberService memberService;
    private final VerificationCodeService verificationCodeService;
    private final ReportService reportService;
    private final BlockService blockService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String loginPage() {
        return "member/login";
    }

    @GetMapping("/myPage")
    public String myPage(Model model) {
        Member currentMember = memberService.getCurrentMember();
        model.addAttribute("member", currentMember);
        return "member/myPage";
    }

    @GetMapping("/myPage/{nickname}")
    public String viewMemberProfile(@PathVariable("nickname") String nickname, Model model) {
        Member member = memberService.findByNickname(nickname);
        if (member == null) {
            return "redirect:/member/notFound"; // 회원이 없으면 "not found" 페이지로 리다이렉트
        }
        model.addAttribute("member", member);
        return "member/viewProfile"; // 프로필 페이지를 반환
    }

    @GetMapping("/modify")
    public String modifyForm(Model model) {
        Member member = memberService.getCurrentMember();
        if (member == null) {
            // 사용자 정보가 없는 경우 로그인 페이지로 리다이렉트
            return "redirect:/member/login";
        }
        model.addAttribute("member", member);
        return "member/modify";
    }

    @PostMapping("/modify")
    public String modify(@RequestParam("phoneNumber") String phoneNumber,
                         @RequestParam("nickname") String nickname,
                         @RequestParam("password") String password,
                         @RequestParam("email") String email,
                         @RequestParam("age") int age,
                         @RequestParam("region") String region,
                         @RequestParam("mbti") String mbti,
                         @RequestParam("sns") String sns,
                         @RequestParam("favoriteFood") String favoriteFood,
                         @RequestParam("thumbnail") MultipartFile thumbnail) {

        // 현재 로그인된 회원 정보를 가져옵니다.
        Member member = memberService.getCurrentMember();
        if (member == null) {
            // 사용자 정보가 없는 경우 로그인 페이지로 리다이렉트
            return "redirect:/member/login";
        }

        // 회원 정보를 수정합니다.
        memberService.modify(member, phoneNumber, nickname, password, age, email, region, favoriteFood, mbti, sns, thumbnail);

        // 수정 완료 후 마이페이지로 리다이렉트
        return "redirect:/member/myPage";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "member/admin";
    }

    @GetMapping("/signup")
    public String signForm(Model model) {
        return "member/signup"; // signup.html을 반환
    }


    @PostMapping("/signup")
    public String signup(@RequestParam("username") String username,
                         @RequestParam("phoneNumber") String phoneNumber,
                         @RequestParam("nickname") String nickname,
                         @RequestParam("password") String password,
                         @RequestParam("email") String email,
                         @RequestParam("age") int age,
                         @RequestParam("gender") String gender,
                         @RequestParam("region") String region,
                         @RequestParam("mbti") String mbti,
                         @RequestParam("sns") String sns,
                         @RequestParam("favoriteFood") String favoriteFood,
                         @RequestParam("thumbnail") MultipartFile thumbnail,
                         HttpSession session
    ) {

        // 이메일 확인용 코드 생성
        String verificationCode = verificationCodeService.generateVerificationCode(email);
        // 회원가입 확인 이메일 보내기

        String subject = "회원가입 인증코드";
        String body = "회원가입인증 코드입니다. : " + verificationCode;
        emailService.send(email, subject, body);

        session.setAttribute("verificationCode", verificationCode);

        // 파일 업로드 성공 시 회원 가입 처리
        memberService.signup(username, phoneNumber, nickname, password, age, email, gender, region, favoriteFood, mbti, sns, thumbnail, MemberRole.USER);

        // 회원 가입 후 로그인 페이지로 리다이렉트
        return "redirect:/member/verifyCode";
    }

    @GetMapping("/verifyCode")
    public String verifyCodeForm(Model model) {
        return "member/verifyCode"; // verifyCode.html을 반환
    }

    @PostMapping("/verifyCode")
    public String verifyCode(@RequestParam("verification") String verificationCode, HttpSession session) {
        String storedVerification = (String) session.getAttribute("verificationCode"); // 올바른 세션 키 사용
        if (verificationCode != null && verificationCode.equals(storedVerification)) {
            session.removeAttribute("verificationCode"); // 세션에서 올바른 키 제거
            return "member/login";
        } else {
            return "member/verifyCode";
        }
    }

    public class LoginRequest {
        private String loginId;
        private String password;

        // Getters and setters
        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @GetMapping("/current")
    public ResponseEntity<Member> getCurrentUser() {
        try {
            Member currentMember = memberService.getCurrentMember();
            return ResponseEntity.ok(currentMember);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }





    //회원 탈퇴
    @GetMapping("/delete")
    public String showDeleteForm(Model model) {
        // 회원 탈퇴 폼을 보여주는 로직
        return "member/delete"; // 탈퇴 확인 폼 페이지로 이동
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response) {
        // memberId를 이용해 회원 삭제 로직을 구현
        memberService.deleteMember(username);

        // 사용자 로그아웃
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // 삭제 후 메인 페이지로 리다이렉트
        return "redirect:/";
    }


    @PreAuthorize("hasRole('admin')")
    @GetMapping("/admin/deleteMember/{username}")
    public String deleteMemberByAdmin(@PathVariable("username") String username, Principal principal) {
        Member admin = memberService.getCurrentMember();

        if (!memberService.isAdmin(admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
        }

        memberService.deleteMemberByAdmin(username);
        return "redirect:/admin/memberList";  // 회원 목록 페이지로 리다이렉트
    }


}
