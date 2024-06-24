package com.example.blog.domain.friend;

import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@RequiredArgsConstructor
@RequestMapping("/friend")
@Controller
public class FriendController {

    private final FriendService friendService;
    private final MemberService memberService;


    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "")String kw) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/member/login"; // 로그인이 되어 있지 않은 경우 로그인 페이지로 리다이렉트
        }


        Member member = memberService.getCurrentMember();

        if (member != null && member.getUsername().equals("admin")) {
            Page<Friend> paging = friendService.getList1(page, kw);
            model.addAttribute("paging", paging);
            model.addAttribute("loggedInUser", member);
        } else {
            String region = member.getRegion();
            Page<Friend> paging = friendService.getList(page, kw, region);
            model.addAttribute("paging", paging);
            model.addAttribute("loggedInUser", member);
        }

        return "friend_list";
    }



    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Friend f = this.friendService.getFriend(id);
        model.addAttribute("friend", f);

        return "friend_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(FriendForm friendForm) {
        return "friend_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String friendCreate(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("capacity") int capacity,
            @RequestParam("cuisineType") String cuisineType,
            @RequestParam("address") String address,
            @RequestParam("restaurantName") String restaurantName,
            @RequestParam("meetingDate") LocalDate meetingDate,
            @RequestParam("meetingTime") LocalTime meetingTime,
            Principal principal) {
        if (title.isEmpty() || content.isEmpty() || cuisineType.isEmpty() || address.isEmpty() || restaurantName.isEmpty()) {
            return "friend_form";
        }

        Member member = memberService.getCurrentMember();
        Friend friend = friendService.create(title,content,capacity,cuisineType,address, restaurantName,  meetingDate , meetingTime,member, member.getRegion());

        return "redirect:/friend/list";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String friendModify(@Valid FriendForm friendForm, BindingResult bindingResult,
                               Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "friend_form";
        }
        Friend friend = friendService.getFriend(id);

        if (!friend.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        friendService.modify(friend,friendForm.getTitle(), friendForm.getContent(), friendForm.getCapacity(), friendForm.getCuisineType(), friendForm.getAddress(), friendForm.getRestaurantName(), friendForm.getMeetingDate(), friendForm.getMeetingTime());

        return "redirect:/friend/detail/%s".formatted(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String friendModify(FriendForm friendForm, @PathVariable("id") Integer id, Principal principal) {
        Friend friend = friendService.getFriend(id);

        if (!friend.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        friendForm.setTitle(friend.getTitle());
        friendForm.setContent(friend.getContent());
        friendForm.setCapacity(friend.getCapacity());
        friendForm.setCuisineType(friend.getCuisineType());
        friendForm.setAddress(friend.getAddress());
        friendForm.setRestaurantName(friend.getRestaurantName());
        friendForm.setMeetingDate(friend.getMeetingDate());
        friendForm.setMeetingTime(friend.getMeetingTime());
        return "friend_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String friendDelete(Principal principal, @PathVariable("id") Integer id) {
        Friend friend = this.friendService.getFriend(id);

        if (!friend.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권힌이 없습니다.");
        }

        friendService.delete(friend);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String friendVote(Principal principal, @PathVariable("id") Integer id) {
        Friend friend = this.friendService.getFriend(id);
        Member siteUser = this.memberService.getCurrentMember();

        this.friendService.vote(friend, siteUser);

        return "redirect:/friend/detail/%s".formatted(id);
    }

    @PostMapping("/increment/{id}")
    public ResponseEntity<Friend> incrementParticipants(@PathVariable Integer id) {
        Friend friend = friendService.incrementParticipants(id);
        return ResponseEntity.ok(friend);
    }
}