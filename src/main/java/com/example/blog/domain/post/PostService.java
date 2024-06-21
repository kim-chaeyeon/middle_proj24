package com.example.blog.domain.post;

import com.example.blog.DataNotFoundException;
import com.example.blog.domain.comment.Comment;
import com.example.blog.domain.member.entity.Member;
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

    public Post create(String title, String content, MultipartFile thumbnail, Member author) {
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
                .author(author)
                .createDate(LocalDateTime.now())
                .thumbnailImg(thumbnailRelPath)
                .build();
        postRepository.save(post);

        return post;
    }

    public List<Post> getPostsByAuthorNickname(String nickname) {
        return postRepository.findByAuthorNickname(nickname);
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

    public void vote(Post post, Member voter) {
        post.addVoter(voter);
        postRepository.save(post);
    }

    private Specification<Post> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Post, Member> u1 = p.join("author", JoinType.LEFT);
                Join<Post, Comment> a = p.join("commentList", JoinType.LEFT);
                Join<Comment, Member> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(p.get("title"), "%" + kw + "%"), // 제목
                        cb.like(p.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Post> getList(int page, String kw, String region) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        Specification<Post> spec = Specification.where(region(region));

        if (kw != null && !kw.trim().isEmpty()) {
            spec = spec.and(search(kw));
        }

        return postRepository.findAll(spec, pageable);
    }

// 특정 지역에 해당하는 게시글을 검색할때 필요함! 이거 없으면 그냥 전처럼 지역 상관없이 다 출력됨
// Specification을 사용하여 데이터베이스 쿼리를 동적으로 생성하기 위한 메서드 -> 이 문장은 이해가 안되.. 찌발..

    private Specification<Post> region(String region) {
        return (root, query, cb) -> cb.equal(root.get("region"), region);
    }

// create도 마찬가지! 지역 추가 해줘야겠지?

    public Post create(String title, String content, MultipartFile thumbnail, Member author, String region) {
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
                .author(author)
                .createDate(LocalDateTime.now())
                .thumbnailImg(thumbnailRelPath)
                .region(region)
                .build();
        postRepository.save(post);

        return post;
    }

    public Page<Post> getPostsByAuthor(Member member, Pageable pageable) {
        return postRepository.findByAuthor(member, pageable);
    }


}

