package com.example.blog.domain.Oatuh.detail;


import com.example.blog.domain.Oatuh.OAuth2UserInfo;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class NaverUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return (String) ((Map) attributes.get("response")).get("id");
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("response")).get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map) attributes.get("response")).get("name");
    }
    @Override
    public String getNickname() {
        return (String) ((Map) attributes.get("response")).get("nickname");
    }

}