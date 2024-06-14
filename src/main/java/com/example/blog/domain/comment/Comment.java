package com.example.blog.domain.comment;

import com.example.blog.domain.post.Post;
import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Member author;

    @ManyToMany
    Set<Member> voters = new LinkedHashSet<>();

    public void addVoter(Member voter) {
        voters.add(voter);
    }
}
