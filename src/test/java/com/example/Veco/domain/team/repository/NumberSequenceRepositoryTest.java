package com.example.Veco.domain.team.repository;

import com.example.Veco.domain.team.entity.NumberSequence;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.enums.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NumberSequenceRepositoryTest {

    @Autowired NumberSequenceRepository numberSequenceRepository;
    @Autowired TeamRepository teamRepository;


    @DisplayName("팀 ID, 카테고리를 통해서 NumberSequence를 조회합니다.")
    @Test
    @Transactional
    void findByTeamIdAndNumberType(){

        //given
        Team team = Team.builder()
                .name("team")
                .build();

        teamRepository.save(team);

        NumberSequence numberSequence = NumberSequence.builder()
                .team(team)
                .category(Category.EXTERNAL)
                .build();

        numberSequenceRepository.save(numberSequence);

        //when
        NumberSequence result
                = numberSequenceRepository.findByTeamIdAndNumberType(team.getId(), Category.EXTERNAL).get();

        //then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTeam().getId()).isEqualTo(team.getId());
    }
}