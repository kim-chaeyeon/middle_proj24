
package com.example.blog.domain.initdata;


import com.example.blog.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Profile("dev")
public class Dev {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner init(MemberService memberService) {
        return args -> {
            // Read profile image file and convert to MultipartFile
            File file = new File("C:\\Users\\SBS\\Desktop\\밥먹냐.png");
            try (FileInputStream input = new FileInputStream(file)) {
                MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", input);

                // Call the signup method
                memberService.signup("user1", "01012345678", "user11", "1234", 0, "admin@test.com"
                        , "남자", "고성", "양식", "대전", "tong", multipartFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            File file1 = new File("C:\\Users\\SBS\\Desktop\\개구리.png");
            try (FileInputStream input = new FileInputStream(file1)) {
                MockMultipartFile multipartFile1 = new MockMultipartFile("file1", file1.getName(), "image/jpeg", input);

                // Call the signup method

                memberService.signup("user2", "01012345678", "user22", "1234", 25,
                        "admin@test.com", "여자", "대전", "일식", "iiii", "holy", multipartFile1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
