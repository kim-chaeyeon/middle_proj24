package com.example.blog.domain.Oatuh;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();

    String getNickname();
}