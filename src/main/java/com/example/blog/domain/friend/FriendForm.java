package com.example.blog.domain.friend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
public class FriendForm {

    // 제목을 설정하는 메서드입니다.
    // 제목을 반환하는 메서드입니다.
    // 게시물의 제목을 저장하는 필드입니다.
    @NotBlank(message = "제목은 필수항목입니다.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    // 내용을 설정하는 메서드입니다.
    // 내용을 반환하는 메서드입니다.
    // 게시물의 내용을 저장하는 필드입니다.
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 20000, message = "내용은 20,000자 이내로 입력해주세요.")
    private String content;

    // 최대 인원 수를 설정하는 메서드입니다.
    // 최대 인원 수를 반환하는 메서드입니다.
    // 게시물에 참여할 수 있는 최대 인원 수를 저장하는 필드입니다.
    @NotBlank(message = "인원수는 필수입니다.")
    private int capacity;

    // 장소를 설정하는 메서드입니다.
    // 장소를 반환하는 메서드입니다.
    // 게시물에서 지정된 장소를 저장하는 필드입니다.
    @NotBlank(message = "장소는 필수입니다.")
    private String location;

    // 음식 종류를 설정하는 메서드입니다.
    // 음식 종류를 반환하는 메서드입니다.
    // 게시물에서 제공될 음식 종류를 저장하는 필드입니다.
    @NotBlank(message = "음식종류는 필수입니다.")
    private String cuisineType;

    @NotBlank(message = "주소를 입력하세요.")
    private String address;

    @NotBlank(message = "식당 이름을 입력해주세요")
    private String restaurantName;

    @NotBlank(message = "날짜를 입력해주세요.")
    private LocalDate meetingDate;

    @NotBlank(message = "시간을 입력해주세요.")
    private LocalTime meetingTime;

}
