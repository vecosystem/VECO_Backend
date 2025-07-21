package com.example.Veco.global.auth.user.userdetails;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.global.auth.user.AuthUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

import java.util.List;

public class CustomUserDetails extends AuthUser implements UserDetails {

    public CustomUserDetails(Member member) {
        super(member.getSocialUid(), member.getEmail(), "");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return super.getSocialUid();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getSocialUid() {
        return super.getSocialUid();
    }
}