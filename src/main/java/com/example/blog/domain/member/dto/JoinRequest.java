package com.example.blog.domain.member.dto;


import com.example.blog.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "ID를 입력하세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
    private String passwordCheck;

<<<<<<< HEAD




=======
>>>>>>> 38ad50beb563f6ee5f8947b6073eef57ddd9963c
    public Member toEntity(){
        return Member.builder()
                .username(username)
                .password(password)
                .build();
    }
}