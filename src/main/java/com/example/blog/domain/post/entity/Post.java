package com.example.blog.domain.post.entity;


import com.example.blog.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Post extends BaseEntity {
    @Id
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;
    @Setter
    private String thumbnailImg;
    private LocalDateTime createDate;
    @Setter
    protected LocalDateTime modifiedDate;
    private String author;
    @Setter
    @Getter
    private int recommendCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

}