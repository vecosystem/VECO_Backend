package com.example.Veco.global.auth.oauth2;

import com.example.Veco.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    Member member;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
                          String nameAttributeKey, Member member) {
        super(authorities, attributes, nameAttributeKey);
        this.member = member;
    }


    public static CustomOAuth2User of(Collection<? extends GrantedAuthority> authorities,
                                    Map<String, Object> attributes,
                                    String nameAttributeKey,
                                    Member member) {
        return new CustomOAuth2User(authorities, attributes, nameAttributeKey, member);
    }
}
