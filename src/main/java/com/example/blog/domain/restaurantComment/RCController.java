package com.example.blog.domain.restaurantComment;

import com.example.blog.domain.restaurant.Restaurant;
import com.example.blog.domain.restaurant.RestaurantService;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
@RequestMapping("/rc")
@Controller
@RequiredArgsConstructor
public class RCController {

    private final RestaurantService restaurantService;
    private final RCService rcService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createRC(
            Model model,
            @PathVariable("id")
            Integer id,
            @Valid RCForm rcForm,
            BindingResult bindingResult,
            Principal principal
    ) {

        Restaurant r = this.restaurantService.getRestaurant(id);
        Member member = this.memberService.getCurrentMember();

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", r);
            return "restaurant_detail";
        }

        RC rc = this.rcService.create(r, rcForm.getContent(), member);

        return "redirect:/restaurant/detail/%d#rc_%d".formatted(id, rc.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String rcModify(RCForm rcForm, @PathVariable("id") Integer id, Principal principal) {
        RC rc = this.rcService.getRC(id);

        if (!rc.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        rcForm.setContent(rc.getContent());

        return "restaurantComment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String rcModify(@Valid RCForm rcForm, BindingResult bindingResult,
                           @PathVariable("id") Integer id, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "restaurantComment_form";
        }

        RC rc = this.rcService.getRC(id);

        if (!rc.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        rcService.modify(rc, rcForm.getContent());

        return "redirect:/restaurant/detail/%d#rc_%d".formatted(rc.getRestaurant().getId(), id);

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String rcDelete(Principal principal, @PathVariable("id") Integer id) {
        RC rc = this.rcService.getRC(id);

        if (!rc.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        rcService.delete(rc);
        return "redirect:/restaurant/detail/%s".formatted(rc.getRestaurant().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String rcVote(Principal principal, @PathVariable("id") Integer id) {
        RC rc = this.rcService.getRC(id);
        Member member = this.memberService.getCurrentMember();

        rcService.vote(rc, member);

        return "redirect:/restaurant/detail/%d#rc_%d".formatted(rc.getRestaurant().getId(), id);
    }
}