package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.apiPayload.exception.VecoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Veco.global.apiPayload.code.ErrorStatus;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;

    @Override
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new VecoException(ErrorStatus._BAD_REQUEST)); // TODO 예외 처리 개선 필요
    }
}
