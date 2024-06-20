package com.example.blog.domain.friend;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateFriendRequest {
    // Setters
    // Getters
    private String title;
    private String content;
    private int capacity;
    private String location;
    private String cuisineType;
    private String username;

}