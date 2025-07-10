package com.example.Veco.domain.team.service;

import com.example.Veco.domain.team.converter.NumberSequenceConverter;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.NumberSequence;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.enums.Category;
import com.example.Veco.domain.team.repository.NumberSequenceRepository;
import com.example.Veco.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NumberSequenceService {

    private final NumberSequenceRepository numberSequenceRepository;
    private final TeamRepository teamRepository;

    public NumberSequenceResponseDTO reserveNextNumber(String workSpaceName, Long teamId, Category category) {
        NumberSequence numberSequence = getOrCreateSequence(teamId, category);

        Long nextNumber = numberSequence.getCurrentNumber() + 1;

        String nextCode = generateCode(workSpaceName, nextNumber, category);

        return NumberSequenceConverter.toResponseDTO(teamId, category, nextCode, false);
    }

    @Transactional
    public NumberSequenceResponseDTO allocateNextNumber(String workSpaceName, Long teamId, Category category) {
        return allocateNumberWithOptimisticLock(teamId, category, workSpaceName);
    }

    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2, maxDelay = 1000)
    )
    private NumberSequenceResponseDTO allocateNumberWithOptimisticLock(
            Long teamId, Category category, String workSpaceName
    ) {

        try{
            NumberSequence numberSequence = getOrCreateSequence(teamId, category);

            Long nextNumber = numberSequence.allocateNumber();

            numberSequenceRepository.save(numberSequence);

            String nextCode = generateCode(workSpaceName, nextNumber, category);

            return NumberSequenceConverter.toResponseDTO(teamId, category, nextCode, true);

        }catch (OptimisticLockingFailureException e){
            throw e;
        }
    }

    private String generateCode(String workSpaceName, Long nextNumber, Category category) {

        String type = "";

        switch (category) {
            case GOAL -> type = "g";
            case ISSUE -> type = "i";
            case EXTERNAL -> type = "e";
            default -> type = "null";
        }

        return String.format("%s-%s%d", workSpaceName, type, nextNumber);
    }

    private NumberSequence getOrCreateSequence(Long teamId, Category category) {
        return numberSequenceRepository.findByTeamIdAndNumberType(teamId, category)
                .orElseGet(() -> createNewSequence(teamId, category));
    }

    private NumberSequence createNewSequence(Long teamId, Category category) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        return NumberSequenceConverter.toNumberSequence(team, category);
    }

}
