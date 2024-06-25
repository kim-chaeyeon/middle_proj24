package com.example.blog.domain.Oatuh.detail;

import com.example.blog.domain.Oatuh.OAuth2UserInfo;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getNickname() {
        return (String) ((Map) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getName() {
        String name = (String) ((Map) attributes.get("properties")).get("name");
        if (name == null || name.isEmpty()) {
            name = getNickname(); // 이름이 없는 경우 닉네임을 반환
        }
        return name;
    }
}
