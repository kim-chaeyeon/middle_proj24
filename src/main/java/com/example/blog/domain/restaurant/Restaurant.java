package com.example.blog.domain.restaurant;

import com.example.blog.domain.restaurantComment.RC;
import com.example.blog.domain.user.SiteUser;
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

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.EXTRA)
    //rcList.size(); 함수가 실행 될 때 SELECT COUNT 실행
    private List<RC> rcList = new ArrayList<>();

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voters = new LinkedHashSet<>();

    public void addVoter(SiteUser voter) {
        voters.add(voter);
    }


    public void setThumbnail(String thumbnailRelPath){

    }
}

