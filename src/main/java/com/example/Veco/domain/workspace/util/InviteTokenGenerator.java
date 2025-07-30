package com.example.Veco.domain.workspace.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InviteTokenGenerator {

    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }
}
