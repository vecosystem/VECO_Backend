package com.example.Veco.global.auth.user.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.auth.user.exception.UserException;
import com.example.Veco.global.auth.user.exception.code.UserErrorCode;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // uid로 사용자 정보 가져오기
    @Override
    public UserDetails loadUserByUsername(String socialUid) throws UsernameNotFoundException {
        Member member = memberRepository.findBySocialUid(socialUid).orElseThrow(() ->
                new UserException(UserErrorCode.USER_NOT_FOUND));
        return new CustomUserDetails(member);
    }
}
