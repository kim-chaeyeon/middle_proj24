package com.example.blog.domain.post;

import com.example.blog.domain.comment.CommentForm;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/post")
@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Post> paging = this.postService.getList(page, kw);
        model.addAttribute("paging", paging);


        return "post_list";
    }

    @GetMapping("/memberPosts/{nickname}")
    public String getPostsByAuthorNickname(@PathVariable String nickname, Model model, @RequestParam(defaultValue = "0") int page) {
        Member member = memberService.findByNickname(nickname);
        if (member == null) {
            return "redirect:/error";
        }
        List<Post> memberPosts = postService.getPostsByAuthorNickname(nickname);
        model.addAttribute("userPosts", memberPosts);
        model.addAttribute("nickname", nickname);
        model.addAttribute("thumbnailImg", member.getThumbnailImg());
        model.addAttribute("region", member.getRegion());
        model.addAttribute("gender", member.getGender());
        model.addAttribute("age", member.getAge());
        return "member_posts";
    }




    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, CommentForm commentForm) {
        Post p = this.postService.getPost(id);

        model.addAttribute("post", p);

        return "post_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(PostForm postForm) {

        return "post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            Principal principal) {

        // 유효성 검사 로직 추가 가능
        if (title.isEmpty() || content.isEmpty() || thumbnail.isEmpty()) {
            // 에러 처리 로직
            return "post_form";
        }

        Member member = this.memberService.getCurrentMember();
        Post p = this.postService.create(title, content, thumbnail, member);

        return "redirect:/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String postModify(@Valid PostForm postForm, BindingResult bindingResult,
                             Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "post_form";
        }

        Post post = postService.getPost(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        postService.modify(post, postForm.getTitle(), postForm.getContent());

        return "redirect:/post/detail/%s".formatted(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(PostForm postForm, @PathVariable("id") Integer id, Principal principal) {
        Post post = postService.getPost(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        return "post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String postDelete(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        postService.delete(post);

        return "redirect:/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String postVote(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        Member member = this.memberService.getCurrentMember();
        this.postService.vote(post, member);

        return "redirect:/post/detail/%s".formatted(id);
    }

    @GetMapping("/myPost")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        Member member = memberService.getCurrentMember();
        Pageable pageable = PageRequest.of(page, 10);  // 한 페이지에 10개의 게시물 표시

        Page<Post> myPosts = postService.getPostsByAuthor(member, pageable);

        model.addAttribute("myPosts", myPosts);
        model.addAttribute("loggedInUser", member);

        return "myPost";
    }
}
