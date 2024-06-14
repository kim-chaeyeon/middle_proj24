package com.example.blog.domain.restaurant;

import com.example.blog.domain.naver.NaverSearchService;
import com.example.blog.domain.naver.dto.SearchLocalRes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantApiController {

    private final NaverSearchService naverSearchService;

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public SearchLocalRes search(@RequestParam String query) {
        return naverSearchService.searchLocal(query);
    }
}
