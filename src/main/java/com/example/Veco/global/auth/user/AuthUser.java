package com.example.Veco.global.auth.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
    private String socialUid;
    private String email;
    private String password;
}