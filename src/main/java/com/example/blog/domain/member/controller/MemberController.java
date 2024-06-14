package com.example.blog.domain.member.controller;


import com.example.blog.domain.email.EmailService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.member.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final VerificationCodeService verificationCodeService;
    private final MemberService memberService;
    private final EmailService emailService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String loginPage() {
        return "member/login";
    }

    //    @GetMapping("/signup")
//    public String signupPage() {
//        return "member/signup";
//    }
    @GetMapping("/myPage")
    public String myPage(Model model) {
        Member currentMember = memberService.getCurrentMember();
        model.addAttribute("member", currentMember);
        return "member/myPage";
    }




    @GetMapping("/signup")
    public String signupForm(Model model) {
        return "member/signup"; // signup.html을 반환
    }



    @PostMapping("/signup")
    public String signup(String username, String phoneNumber, String nickname, String password,
                         String email, int age, String gender, String region, String favoriteFood, Model model) {
        try {
            memberService.signup(username, phoneNumber, nickname, password, email, age, gender, region, favoriteFood);

            String subject = " 회원가입!";
            String body = "회원가입 성공 이메일!" + LocalDateTime.now();;
            emailService.send(email, subject, body);


            return "member/login"; // 회원 가입 후 로그인 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 가입 중 오류가 발생했습니다.");
            return "member/signup"; // 오류 발생 시 다시 회원 가입 페이지로
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


    @GetMapping("/sendVerificationEmail")
    public String showEmailForm() {
        return "member/sendVerificationEmail";
    }

    @PostMapping("/sendVerificationEmail")
    public ResponseEntity<Map<String, Object>> sendVerificationEmail(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 이메일로 인증 코드 생성 및 전송
            String verificationCode = verificationCodeService.generateVerificationCode();
            emailService.send(email, "이메일 인증 코드", "인증 코드: " + verificationCode);

            // 이메일과 인증 코드를 세션에 저장 (생략)
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 발생 시 클라이언트에게 오류 응답 전송
            response.put("success", false);
            response.put("error", "오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/verifyCode")
    public String showVerificationForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "member/verifyCode";
    }

    @PostMapping("/verifyCode")
    public String verifyCode(@RequestParam("email") String email, @RequestParam("code") String code, @ModelAttribute("verificationCode") String sessionCode, Model model) {
        if (sessionCode != null && sessionCode.equals(code)) {
            return "member/signup";
        } else {
            model.addAttribute("error", "인증 코드가 잘못되었습니다.");
            return "member/verifyCode";
        }
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("verificationCode") String verificationCode) {
        // 인증 코드 확인
        boolean isValid = verificationCodeService.verifyVerificationCode(verificationCode);

        if (isValid) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다. 회원가입을 완료하세요.");
        } else {
            return ResponseEntity.badRequest().body("유효하지 않은 인증 코드입니다.");
        }
    }


}
