package com.example.Veco.domain.member.error;

import com.example.Veco.global.apiPayload.exception.VecoException;

public class MemberHandler extends VecoException {
    public MemberHandler(String message) {
        super(message);
    }
}
