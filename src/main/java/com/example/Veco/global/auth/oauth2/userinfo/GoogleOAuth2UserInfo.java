package com.example.Veco.global.auth.oauth2.userinfo;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getSocialId() { return (String) attributes.get("sub"); }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

}
