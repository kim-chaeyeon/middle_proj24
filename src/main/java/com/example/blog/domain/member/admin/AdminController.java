package com.example.blog.domain.member.admin;

import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;

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

    @GetMapping("/global/list")
    public String showGlobalList(Model model){

        return "global/list";
    }
}