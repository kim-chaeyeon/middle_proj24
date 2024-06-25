package com.example.blog.domain.member.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    @Comment("유저 아이디")
    private String loginId;

    private String password;

    @Column(unique = true)
    @Comment("유저 닉네임")
    private String nickname;

    @Column(unique = true)
    private String username; // 이 줄을 추가하세요.

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    // provider : google이 들어감
    private String provider;

    // providerId : 구글 로그인 한 유저의 고유 ID가 들어감
    private String providerId;

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

    @Comment("사진")
    private String thumbnailImg;

    @Comment("mbti")
    private String mbti;

    @Comment("sns")
    private String sns;

    private String email;

    private String googleId;
    private String naverId;

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getNaverId() {
        return naverId;
    }

    public void setNaverId(String naverId) {
        this.naverId = naverId;
    }

    private boolean admin;

    public String getThumbnailImg() {
        return thumbnailImg;
    }

    public void setThumbnailImg(String thumbnailImg) {
        this.thumbnailImg = thumbnailImg;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }



}

