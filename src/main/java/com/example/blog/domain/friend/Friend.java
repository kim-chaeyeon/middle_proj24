package com.example.blog.domain.friend;


import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend {


    // Setters
    // Getters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content; //추천음식

    private int capacity;
    private int currentParticipants;
    private String address; //주소
    private  String restaurantName; //식당이름
    private String cuisineType;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ElementCollection
    private List<String> addressList;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    @ManyToMany
    Set<Member> voters = new LinkedHashSet<>();

    public void addVoter(Member voter) {
        voters.add(voter);
    }

}