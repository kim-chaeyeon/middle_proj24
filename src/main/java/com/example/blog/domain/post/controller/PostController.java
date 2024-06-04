package com.example.blog.domain.post.controller;

import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Post> postList = postService.getList();
        model.addAttribute("postList", postList);
        return "post/list";
    }

    @GetMapping("/create")
    public String create() {
        return "post/create_form";
    }

    @PostMapping("/create")
    public String create(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("thumbnail") MultipartFile thumbnail) {
        postService.create(title, content, thumbnail);
        return "redirect:/post/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPost(id);
        model.addAttribute("post", post);
        return "post/detail";
    }

    // 게시물 수정 폼으로 이동
    @GetMapping("/modify/{id}")
    public String modifyForm(@PathVariable("id") Long id, Model model, Principal principal) {
        Post post = postService.getPost(id);
        //if (!post.getAuthor().equals(principal.getName())) {
          //  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        //}
        model.addAttribute("post", post);
        return "post/modify_form";
    }

    // 게시물 수정 처리
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable("id") Long id,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                         Principal principal) {
        Post post = postService.getPost(id);
        ///if (!post.getAuthor().equals(principal.getName())) {
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        //}
        postService.modify(post, title, content, thumbnail);
        return "redirect:/post/" + id;
    }

    // 게시물 삭제 처리
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal) {
        Post post = postService.getPost(id);
       // if (!post.getAuthor().equals(principal.getName())) {
         //   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        //}
        postService.delete(id);
        return "redirect:/post/list";
    }

    @PostMapping("/post/recommend/{id}")
    public ResponseEntity<Map<String, Integer>> recommendPost(@PathVariable Long id) {
        int recommendCount = postService.incrementRecommendCount(id);
        Map<String, Integer> response = new HashMap<>();
        response.put("recommendCount", recommendCount);
        return ResponseEntity.ok(response);
    }
}