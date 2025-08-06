package com.example.Veco.domain.team.entity;

import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private Category category;

    private Long currentNumber;

    @Version
    private Long version;

    public synchronized Long allocateNumber() {
        this.currentNumber += 1;
        return this.currentNumber;
    }

}
