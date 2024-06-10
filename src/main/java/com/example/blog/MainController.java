package com.example.blog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/sbb")
    public String index() {
        return "list";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/post/list";
    }
}