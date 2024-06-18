package com.example.blog.domain.friend;

import com.example.blog.domain.user.SiteUser;
import com.example.blog.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/friend")
@Controller
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;


    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "")String kw) {
        Page<Friend> paging = this.friendService.getList(page, kw);
        model.addAttribute("paging", paging);

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
            Principal principal) {
        if (title.isEmpty() || content.isEmpty() || cuisineType.isEmpty() || address.isEmpty() || restaurantName.isEmpty()) {
            return "friend_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        Friend f = this.friendService.create(title,content,capacity,cuisineType,address, restaurantName,siteUser);

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

        friendService.modify(friend,friendForm.getTitle(), friendForm.getContent(), friendForm.getCapacity(), friendForm.getCuisineType(), friendForm.getAddress(), friendForm.getRestaurantName());

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
        return "friend_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String friendDelete(Principal principal, @PathVariable("id") Integer id) {
        Friend friend = this.friendService.getFriend(id);

        if (!friend.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권힌ㅇ; 없습니다.");
        }

        friendService.delete(friend);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String friendVote(Principal principal, @PathVariable("id") Integer id) {
        Friend friend = this.friendService.getFriend(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        this.friendService.vote(friend, siteUser);

        return "redirect:/friend/detail/%s".formatted(id);
    }

    @PostMapping("/increment/{id}")
    public ResponseEntity<Friend> incrementParticipants(@PathVariable Integer id) {
        Friend friend = friendService.incrementParticipants(id);
        return ResponseEntity.ok(friend);
    }
    // Additional endpoints for retrieving, updating, deleting friends
}

