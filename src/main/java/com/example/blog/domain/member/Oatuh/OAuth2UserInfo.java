package com.example.blog.domain.member.Oatuh;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();

    String getNickname();
}