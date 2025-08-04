package com.example.Veco.domain.comment.entity;

import com.example.Veco.domain.common.BaseEntity;

import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment_room", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"room_type", "target_id"}))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category roomType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @OneToMany(mappedBy = "commentRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

}
