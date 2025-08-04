package com.example.Veco.global.auth.user;

import com.example.Veco.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
    private String socialUid;
    private String email;
    private String password;
}