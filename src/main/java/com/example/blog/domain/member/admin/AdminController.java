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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final MemberService memberService;
    private final ReportService reportService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/page")
    public String showAdminPage(Model model) {
        return "admin/admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/members")
    public String showAllMembers(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        model.addAttribute("pageName", "회원 목록");
        return "admin/members";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteMember/{username}")
    public String deleteMemberByAdmin(@PathVariable("username") String username) {
        try {
            memberService.deleteMemberByAdmin(username);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다. ID: " + username);
        }
        return "redirect:/admin/members";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/reports")
    public String showAllReports(Model model) {
        List<Report> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "admin/reports";
    }
}
