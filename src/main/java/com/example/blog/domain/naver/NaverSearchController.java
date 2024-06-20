package com.example.blog.domain.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
public class NaverSearchController {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @GetMapping(value = "/naver/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> naverSearchList(@RequestParam(name = "query") String query) {

        // API 호출 URI 설정
        String uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/local.json")
                .queryParam("query", query)
                .queryParam("display", 5)
                .queryParam("start", 1)
                .queryParam("sort", "random")
                .build()
                .toUriString();

        // WebClient를 사용하여 API 호출
        WebClient webClient = WebClient.builder()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();

        // API 호출 및 결과 반환
        String responseBody = webClient.get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(responseBody);
    }
}
