package com.example.Veco.domain.member.service;

import com.example.Veco.domain.member.entity.Member;

public interface MemberCommandService {

    //String updateNickname(Long memberId, String nickname);

    Member saveMember(Member member);
}
