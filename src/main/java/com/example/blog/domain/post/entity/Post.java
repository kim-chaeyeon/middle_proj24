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
    @Setter
    private String title;
    @Setter
    private String content;
    @Setter
    private String thumbnailImg;
    private LocalDateTime createDate;
    private String author;

    public void setModifiedDate(LocalDateTime now) {
        this.modifiedDate = now;
    }

}