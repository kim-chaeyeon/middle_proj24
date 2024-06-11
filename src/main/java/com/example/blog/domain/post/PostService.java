package com.example.blog.domain.post;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.comment.Comment;
import com.example.blog.domain.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    @Value("${custom.fileDirPath}")
    private String fileDirPath;

    public List<Post> getList() {
        return postRepository.findAll();
    }

    public Post getPost(Integer id) {
        Optional<Post> op = postRepository.findById(id);
        if (op.isEmpty()) throw new DataNotFoundException("post not found");
        return op.get();
    }

    public Post create(String title, String content, MultipartFile thumbnail, SiteUser user) {
        String thumbnailRelPath = "post/" + UUID.randomUUID().toString() + ".jpg";
        File thumbnailFile = new File(fileDirPath + "/" + thumbnailRelPath);

        try {
            thumbnail.transferTo(thumbnailFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Post post = Post.builder()
                .title(title)
                .content(content)
                .author(user)
                .createDate(LocalDateTime.now())
                .thumbnailImg(thumbnailRelPath)
                .build();
        postRepository.save(post);

        return post;
    }

    public Page<Post> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (kw == null || kw.trim().length() == 0) {
            return postRepository.findAll(pageable);
        }
        Specification<Post> spec = search(kw);
        return postRepository.findAll(spec, pageable);
    }

    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);
        post.setModifyDate(LocalDateTime.now());
        postRepository.save(post);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public void vote(Post post, SiteUser voter) {
        post.addVoter(voter);
        postRepository.save(post);
    }

    private Specification<Post> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Post, SiteUser> u1 = p.join("author", JoinType.LEFT);
                Join<Post, Comment> a = p.join("commentList", JoinType.LEFT);
                Join<Comment, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(p.get("title"), "%" + kw + "%"), // 제목
                        cb.like(p.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }
}
