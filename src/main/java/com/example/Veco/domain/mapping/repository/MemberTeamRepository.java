package com.example.Veco.domain.mapping.repository;

import com.example.Veco.domain.mapping.entity.MemberTeam;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {

    List<MemberTeam> findAllByMemberIdInAndTeamId(List<Long> memberId, Long teamId);

    List<MemberTeam> findByMemberId(Long memberId);

    Optional<MemberTeam> findByMemberIdAndTeamId(Long memberId, Long teamId);

    List<MemberTeam> findAllByTeamId(Long teamId);

    @Query("SELECT mt.team.id, COUNT(mt.id) FROM MemberTeam mt WHERE mt.team.id IN :teamIds GROUP BY mt.team.id")
    List<Object[]> countMembersByTeamIds(@Param("teamIds") List<Long> teamIds);
}
