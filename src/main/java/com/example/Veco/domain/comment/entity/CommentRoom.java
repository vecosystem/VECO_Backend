package com.example.Veco.domain.comment.entity;

import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment_room")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    private Category roomType;
}
