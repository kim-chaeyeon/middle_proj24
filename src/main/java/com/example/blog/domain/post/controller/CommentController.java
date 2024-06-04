package com.example.blog.domain.post.controller;

import com.example.blog.domain.post.entity.Post;
import com.example.blog.domain.post.service.CommentService;
import com.example.blog.domain.post.service.PostService;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final PostService postService;
    private final CommentService commentService;

    @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Long id, @RequestParam(value="content") String content) {
        Post post = this.postService.getPost(id);
        this.commentService.create(post, content);
        // TODO: 댓글을 저장한다.
        return String.format("redirect:/post/%s", id);
    }
}