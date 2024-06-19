package com.example.blog.domain.member.admin;

import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import com.example.blog.domain.report.entity.Report;
import com.example.blog.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;
    private final ReportService reportService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/page")
    public String showadmin(Model model) {
//        List<Member> members = memberService.getAllMembers();
//        model.addAttribute("members", members);
//        model.addAttribute("pageName", "회원 목록");
        return "admin/admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/members")
    public String showAllMembers(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        model.addAttribute("pageName", "회원 목록");
        return "admin/members";
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/deleteMember/{username}")
    public String deleteMemberByAdmin(@PathVariable("username") String username, Principal principal) {
        Member admin = memberService.getCurrentMember();

        if (!memberService.isAdmin(admin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
        }

        memberService.deleteMemberByAdmin(username);
        return "admin/members";

    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/reports")
    public String showAllReports(Model model) {
        List<Report> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "admin/reports";
    }
}