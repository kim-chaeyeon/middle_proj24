
package com.example.blog.domain.member.controller;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.block.service.BlockService;
import com.example.blog.domain.email.EmailService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.member.service.VerificationCodeService;
import com.example.blog.domain.post.Post;
import com.example.blog.domain.report.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

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
        if (currentMember == null) {
            logger.warn("No current member found in myPage method");
            return "redirect:/member/login";
        }
        model.addAttribute("member", currentMember);
        return "member/myPage";
    }

    @GetMapping("/myPage/{nickname}")
    public String viewMemberProfile(@PathVariable("nickname") String nickname, Model model) {
        Member member = memberService.findByNickname(nickname);
        if (member == null) {
            logger.warn("Member not found for nickname: {}", nickname);
            return "redirect:/member/notFound";
        }
        model.addAttribute("member", member);
        return "member/viewProfile";
    }

    @GetMapping("/modify")
    public String modifyForm(Model model) {
        Member member = memberService.getCurrentMember();
        if (member == null) {
            logger.warn("No current member found in modifyForm method");
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
                         @RequestParam("thumbnail") MultipartFile thumbnail,
                         @RequestParam("gender") String gender) {

        // 현재 로그인된 회원 정보를 가져옵니다.
        Member member = memberService.getCurrentMember();
        if (member == null) {
            // 사용자 정보가 없는 경우 로그인 페이지로 리다이렉트
            return "redirect:/member/login";
        }

        // 회원 정보를 수정합니다.
        memberService.modify(member, phoneNumber, nickname, password, age, email, region, favoriteFood, mbti, sns, thumbnail,gender);

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
                         HttpSession session) {

        String verificationCode = verificationCodeService.generateVerificationCode(email);
        String subject = "회원가입 인증코드";
        String body = "회원가입인증 코드입니다. : " + verificationCode;
        emailService.send(email, subject, body);

        session.setAttribute("verificationCode", verificationCode);
        memberService.signup(username, phoneNumber, nickname, password, age, email, gender, region, favoriteFood, mbti, sns, thumbnail, MemberRole.USER);

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

    public static class LoginRequest {
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
            if (currentMember == null) {
                logger.warn("No current member found in getCurrentUser method");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(currentMember);
        } catch (Exception e) {
            logger.error("Exception in getCurrentUser method", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/delete")
    public String showDeleteForm(Model model) {
        return "member/delete"; // 탈퇴 확인 폼 페이지로 이동
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response) {
        memberService.deleteMember(username);
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/deleteMember/{username}")
    public String deleteMemberByAdmin(@PathVariable("username") String username) {
        Member admin = memberService.getCurrentMember();
        if (admin == null || !memberService.isAdmin(admin)) {
            logger.warn("Unauthorized access attempt by user: {}", admin != null ? admin.getUsername() : "unknown");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
        }

        memberService.deleteMemberByAdmin(username);
        return "redirect:/admin/memberList";  // 회원 목록 페이지로 리다이렉트
    }

    @PostMapping("/report")
    @ResponseBody
    public ResponseEntity<Void> reportMember(@RequestParam("reportedNickname") String reportedNickname,
                                             @RequestParam("reason") String reason) {
        Member reporter = memberService.getCurrentMember();
        if (reporter == null) {
            logger.warn("Unauthorized report attempt by anonymous user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member reported = memberService.findByNickname(reportedNickname);
        if (reported != null) {
            reportService.reportMember(reporter, reported, reason);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Reported member not found: {}", reportedNickname);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/block")
    @ResponseBody
    public ResponseEntity<Void> blockMember(@RequestParam("blockedNickname") String blockedNickname) {
        Member blocker = memberService.getCurrentMember();
        if (blocker == null) {
            logger.warn("Unauthorized block attempt by anonymous user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member blocked = memberService.findByNickname(blockedNickname);
        if (blocked != null) {
            blockService.blockMember(blocker, blocked);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Blocked member not found: {}", blockedNickname);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/unblock")
    @ResponseBody
    public ResponseEntity<Void> unblockMember(@RequestParam("blockedNickname") String blockedNickname) {
        Member blocker = memberService.getCurrentMember();
        if (blocker == null) {
            logger.warn("Unauthorized unblock attempt by anonymous user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member blocked = memberService.findByNickname(blockedNickname);
        if (blocked != null) {
            blockService.unblockMember(blocker, blocked);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Unblocked member not found: {}", blockedNickname);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/posts/{nickname}")
    @ResponseBody
    public ResponseEntity<List<Post>> getPostsByNickname(@PathVariable String nickname, Principal principal) {
        if (principal == null || principal.getName() == null) {
            logger.warn("Unauthorized access attempt to posts by anonymous user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentUsername = principal.getName();
        if (blockService.isBlocked(currentUsername, nickname)) {
            logger.warn("Access attempt to blocked user posts: {}", nickname);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Post> posts = memberService.getPostsByNickname(nickname);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/isBlocked")
    @ResponseBody
    public ResponseEntity<Boolean> isBlocked(@RequestParam("nickname") String nickname, Principal principal) {
        if (principal == null || principal.getName() == null) {
            logger.warn("Unauthorized access attempt to isBlocked by anonymous user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentUsername = principal.getName();
        boolean isBlocked = blockService.isBlocked(currentUsername, nickname);
        return ResponseEntity.ok(isBlocked);
    }


    // 아이디 찾기 시작
    @GetMapping("/find-username")
    public String showFindUsernamePage(Model model) {
        model.addAttribute("username", null); // 초기화: 아이디 결과 없음
        model.addAttribute("error", null); // 초기화: 에러 메시지 없음
        return "member/find-username"; // templates/user/find-username.html 경로에 파일이 있어야 함
    }

    // 아이디 찾기 기능 처리
    @PostMapping("/find-username")
    public String processFindUsername(@RequestParam("email") String email, Model model) {
        try {
            String username = memberService.findUsernameByEmail(email); // 이메일로 아이디 찾기
            model.addAttribute("username", username); // 결과를 모델에 추가
            model.addAttribute("error", null); // 에러 메시지 초기화
        } catch (DataNotFoundException e) {
            model.addAttribute("username", null); // 아이디 결과 초기화
            model.addAttribute("error", "해당 이메일로 등록된 사용자가 없습니다."); // 에러 메시지 설정
        }
        return "member/find-username"; // 결과 표시 페이지로 이동
    }

    // 아이디 찾기 끝

    // 인증 코드 요청 폼 표시
    @GetMapping("/request-reset")
    public String showRequestResetForm() {
        return "member/request-reset";
    }

    // 인증 코드 요청 처리
    @PostMapping("/request-reset")
    public String sendVerificationCode(@RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       Model model) {
        try {
            String verificationCode = verificationCodeService.generateVerificationCode(email);
            emailService.sendVerificationCode(email, "비밀번호 재설정 인증 코드", "비밀번호를 재설정하려면 다음 인증 코드를 입력하세요:", verificationCode);

            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("message", "이메일로 인증 코드가 전송되었습니다.");
            return "member/verify-reset"; // 인증 코드 입력 폼으로 리턴
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "member/request-reset"; // 에러 발생 시 다시 요청 폼으로 리턴
        }
    }

    // 인증 코드 확인 및 비밀번호 재설정
    @PostMapping("/verify-reset")
    public String verifyReset(@RequestParam("username") String username,
                              @RequestParam("email") String email,
                              @RequestParam("verificationCode") String verificationCode,
                              @RequestParam("newPassword") String newPassword,
                              Model model) {
        try {
            if (verificationCodeService.verifyCode(email, verificationCode)) {
                memberService.resetPassword(username, email, newPassword);
                model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
                return "redirect:/member/login"; // 성공 시 로그인 페이지로 리다이렉트
            } else {
                model.addAttribute("error", "인증 코드가 일치하지 않습니다.");
                model.addAttribute("username", username);
                model.addAttribute("email", email);
                return "member/verify-reset"; // 인증 코드 입력 폼으로 리턴
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "member/verify-reset"; // 에러 발생 시 다시 인증 코드 입력 폼으로 리턴
        }
    }

    // 인증 코드 입력 폼 표시
    @GetMapping("/verify-reset")
    public String showVerifyResetForm(@RequestParam("username") String username,
                                      @RequestParam("email") String email,
                                      Model model) {
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        return "member/verify-reset";
    }

}
