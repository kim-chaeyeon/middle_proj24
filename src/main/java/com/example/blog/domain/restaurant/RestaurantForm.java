package com.example.blog.domain.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RestaurantForm {

    @NotBlank(message = "제목은 필수항목입니다.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 20000, message = "내용은 20,000자 이내로 입력해주세요.")
    private String content;

    @NotBlank(message = "사진은 필수입니다.")
    private MultipartFile thumbnail;

    @NotBlank(message = "음식 종류를 선택하세요.")
    private String cuisineType;

//    @NotBlank(message = "주소를 입력하세요.")
//    private String address;

//    @NotBlank(message = "추천 읍식을 적어주세요.")
//    private String recommendedDish;
}