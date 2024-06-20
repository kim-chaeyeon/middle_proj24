package com.example.blog.domain.restaurantComment;

import com.example.blog.domain.restaurant.Restaurant;
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
public class RC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    // Many = RC, one = Restaurant
    @ManyToOne
    private Restaurant restaurant;

    @ManyToOne
    private Member author;

    @ManyToMany
    Set<Member> voters = new LinkedHashSet<>();

    public void addVoter(Member voter) {
        voters.add(voter);
    }
}