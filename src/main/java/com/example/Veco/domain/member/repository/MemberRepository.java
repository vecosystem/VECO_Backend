package com.example.Veco.domain.member.repository;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);

    Optional<Member> findBySocialUid(String socialUid);
    List<Member> findAllByIdIn(List<Long> ids);
}
