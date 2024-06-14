package com.example.blog.domain.restaurant;

import com.example.blog.domain.restaurantComment.RC;
import com.example.blog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String thumbnailImg;

    private String cuisineType;

    private String address;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<RC> rcList = new ArrayList<>();

    @ManyToOne
    private Member author;

    @ManyToMany
    Set<Member> voters = new LinkedHashSet<>();

    public void addVoter(Member voter) {
        voters.add(voter);
    }
}
