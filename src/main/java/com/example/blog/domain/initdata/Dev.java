package com.example.blog.domain.initdata;


import com.example.blog.domain.member.entity.MemberRole;
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
            File file = new File("C:\\Users\\82109\\Desktop\\박진영.jpg");
            try (FileInputStream input = new FileInputStream(file)) {
                MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", input);

                // Call the signup method

                memberService.signup("admin", "admin", "admin", "1234", 25,
                        "admin@test.com", "admin", "admin", "admin", "admin", "admin", multipartFile, MemberRole.ADMIN);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file1 = new File("C:\\Users\\82109\\Desktop\\예지.jpg");
            try (FileInputStream input = new FileInputStream(file1)) {
                MockMultipartFile multipartFile1 = new MockMultipartFile("file1", file1.getName(), "image/jpeg", input);

                // Call the signup method
                memberService.signup("user2", "01012345678", "예지", "1234", 25,
                        "coco@test.com", "여자", "대전", "일식", "iiii", "holy", multipartFile1, MemberRole.USER);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file2 = new File("C:\\Users\\82109\\Desktop\\유나.jpg");
            try (FileInputStream input = new FileInputStream(file2)) {
                MockMultipartFile multipartFile2 = new MockMultipartFile("file2", file2.getName(), "image/jpeg", input);

                // Call the signup method

                memberService.signup("user1", "01012345678", "유나", "1234", 0, "cake@test.com",
                        "남자", "고성", "양식", "대전", "tong", multipartFile2, MemberRole.USER);

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

}