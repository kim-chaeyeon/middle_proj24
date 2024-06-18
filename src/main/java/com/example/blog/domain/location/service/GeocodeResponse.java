package com.example.blog.domain.location.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GeocodeResponse {
    private List<Result> results;
    private String status;

    @Setter
    @Getter
    public static class Result {
        private Geometry geometry;

        // Getters and setters
    }

    @Setter
    @Getter
    public static class Geometry {
        private Location location;

        // Getters and setters
    }

    @Setter
    @Getter
    public static class Location {
        private double lat;
        private double lng;

        // Getters and setters
    }
}
