package com.example.blog.domain.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotBlank(message = "댓글을 입력하세요.")
    @Size(max = 20000, message = "댓글은 20,000자 이내로 입력해주세요.")
    private String content;
}
