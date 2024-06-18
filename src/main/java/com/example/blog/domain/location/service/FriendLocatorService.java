package com.example.blog.domain.location.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class FriendLocatorService {

    private final GoogleMapsService googleMapsService;

    public FriendLocatorService(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }

    public Flux<Friend> findNearbyFriends(String address, List<Friend> friends) {
        return googleMapsService.getGeocode(address)
                .flatMapMany(geoLocation -> Flux.fromIterable(friends)
                        .filter(friend -> isWithinOneKilometer(geoLocation, friend.location())));
    }

    private boolean isWithinOneKilometer(GoogleMapsService.GeoLocation location1, GoogleMapsService.GeoLocation location2) {
        double earthRadius = 6371e3; // meters
        double lat1 = Math.toRadians(location1.latitude());
        double lat2 = Math.toRadians(location2.latitude());
        double deltaLat = Math.toRadians(location2.latitude() - location1.latitude());
        double deltaLng = Math.toRadians(location2.longitude() - location1.longitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = earthRadius * c;

        return distance <= 1000;
    }

    public record Friend(String name, GoogleMapsService.GeoLocation location) {

    }
}

