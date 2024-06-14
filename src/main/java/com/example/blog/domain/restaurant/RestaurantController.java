package com.example.blog.domain.restaurant;

import com.example.blog.domain.restaurantComment.RCForm;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/restaurant")
@Controller
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Restaurant> paging = this.restaurantService.getList(page, kw);
        model.addAttribute("paging", paging);

        return "restaurant_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, RCForm rcForm) {
        Restaurant r = this.restaurantService.getRestaurant(id);
        model.addAttribute("restaurant", r);

        return "restaurant_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(RestaurantForm restaurantForm) {
        return "restaurant_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String restaurantCreate(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("cuisineType") String cuisineType,
            @RequestParam("address") String address,
            Principal principal) {

        if (title.isEmpty() || content.isEmpty() || thumbnail.isEmpty() || cuisineType.isEmpty() || address.isEmpty()) {
            return "restaurant_form";
        }

        Member member = this.memberService.getCurrentMember();
        Restaurant r = this.restaurantService.create(title, content, thumbnail, cuisineType, address, member);

        return "redirect:/restaurant/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String restaurantModify(@Valid RestaurantForm restaurantForm, BindingResult bindingResult,
                                   Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "restaurant_form";
        }

        Restaurant restaurant = restaurantService.getRestaurant(id);
        Member member = memberService.getCurrentMember();

        if (!restaurant.getAuthor().getUsername().equals(member.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        restaurantService.modify(restaurant, restaurantForm.getTitle(), restaurantForm.getContent(), restaurantForm.getCuisineType(), restaurantForm.getAddress());

        return "redirect:/restaurant/detail/%s".formatted(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String restaurantModify(RestaurantForm restaurantForm, @PathVariable("id") Integer id, Principal principal) {
        Restaurant restaurant = restaurantService.getRestaurant(id);
        Member member = memberService.getCurrentMember();

        if (!restaurant.getAuthor().getUsername().equals(member.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        restaurantForm.setTitle(restaurant.getTitle());
        restaurantForm.setContent(restaurant.getContent());
        restaurantForm.setCuisineType(restaurant.getCuisineType());
        restaurantForm.setAddress(restaurant.getAddress());
        return "restaurant_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String restaurantDelete(Principal principal, @PathVariable("id") Integer id) {
        Restaurant restaurant = this.restaurantService.getRestaurant(id);
        Member member = memberService.getCurrentMember();

        if (!restaurant.getAuthor().getUsername().equals(member.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        restaurantService.delete(restaurant);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String restaurantVote(Principal principal, @PathVariable("id") Integer id) {
        Restaurant restaurant = this.restaurantService.getRestaurant(id);
        Member member = memberService.getCurrentMember();

        this.restaurantService.vote(restaurant, member);

        return "redirect:/restaurant/detail/%s".formatted(id);
    }
}
