package com.example.blog.domain.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class PostForm {

    @NotBlank(message = "제목은 필수항목입니다.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 20000, message = "내용은 20,000자 이내로 입력해주세요.")
    private String content;

    private MultipartFile thumbnail;
}
