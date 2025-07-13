package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.converter.MemberConverter;
import com.example.Veco.domain.member.dto.MemberRequestDTO;
import com.example.Veco.domain.member.dto.MemberResponseDTO;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.apiPayload.exception.VecoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;

    /*
    @Override
    public String updateNickname(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new VecoException("에러")); // 에러 처리 구현 필요

        member.updateNickname(nickname);
        return member.getNickname();
    }*/
}
