package com.example.Veco.domain.mapping;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {

    List<MemberTeam> findAllByMemberIdInAndTeamId(List<Long> memberId, Long teamId);

    List<MemberTeam> findByMemberId(Long memberId);

    Optional<MemberTeam> findByMemberIdAndTeamId(Long memberId, Long teamId);

    List<MemberTeam> findAllByTeamId(Long teamId);
}
