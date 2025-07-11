package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;

public interface MemberQueryService {
    Member findById(Long memberId);
}
