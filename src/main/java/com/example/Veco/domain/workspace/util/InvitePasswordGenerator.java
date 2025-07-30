package com.example.Veco.domain.workspace.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class InvitePasswordGenerator {

    private static final int PASSWORD_LENGTH = 6;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
