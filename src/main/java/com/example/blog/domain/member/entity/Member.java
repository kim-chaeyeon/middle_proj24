package com.example.blog.domain.member.entity;


import com.example.blog.domain.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;



@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Comment("유저 아이디")
    @Column(unique = true)
    private String username;

    private String password;

    @Comment("유저 닉네임")
    @Column(unique = true)
    private String nickname;

    private String email;

    @Comment("전화번호")
    private String phoneNumber;

    @Comment("성별")
    private String gender;

    @Comment("나이")
    private int age; // 나이 필드 추가

    @Comment("지역")
    private String region;

    @Comment("선호 음식")
    private String favoriteFood;

//    @Comment("사진")
//    private String thumbnailImg;

}