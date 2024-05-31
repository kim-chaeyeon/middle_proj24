package com.example.blog.domain.post.entity;


import com.example.blog.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Post extends BaseEntity {
    private Long id;
    private String title;
    private String content;
    private String thumnailImg;
    private LocalDateTime createDate;
    private String author;
}