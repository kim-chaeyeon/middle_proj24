package com.example.blog.domain.naver;

import com.example.blog.domain.naver.NaverClient;
import com.example.blog.domain.naver.dto.SearchLocalReq;
import com.example.blog.domain.naver.dto.SearchLocalRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NaverSearchService {
    private final NaverClient naverClient;

    public SearchLocalRes searchLocal(String query) {
        SearchLocalReq searchLocalReq = new SearchLocalReq();
        searchLocalReq.setQuery(query);
        searchLocalReq.setDisplay(5); // 결과를 5개만 표시
        return naverClient.searchLocal(searchLocalReq);
    }
}
