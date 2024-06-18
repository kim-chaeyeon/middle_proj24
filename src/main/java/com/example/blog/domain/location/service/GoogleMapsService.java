package com.example.blog.domain.location.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GoogleMapsService {

    private final WebClient webClient;

    @Value("${google.api.key}")
    private String apiKey;

    public GoogleMapsService(WebClient.Builder webClientBuilder, @Value("${google.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://maps.googleapis.com/maps/api")
                .build();
        this.apiKey = apiKey;
    }

    public Mono<GeoLocation> getGeocode(String address) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geocode/json")
                        .queryParam("address", address)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(GeocodeResponse.class)
                .flatMap(response -> {
                    if (!response.getResults().isEmpty()) {
                        GeocodeResponse.Result result = response.getResults().get(0);
                        double lat = result.getGeometry().getLocation().getLat();
                        double lng = result.getGeometry().getLocation().getLng();
                        return Mono.just(new GeoLocation(lat, lng));
                    } else {
                        return Mono.error(new RuntimeException("No results found"));
                    }
                })
                .doOnError(error -> log.error("Error retrieving geocode: {}", error.getMessage()));
    }

    public record GeoLocation(double latitude, double longitude) {

    }

    // GeocodeResponse and other nested classes here...
}
