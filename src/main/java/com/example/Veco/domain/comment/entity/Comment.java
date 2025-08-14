package com.example.Veco.domain.comment.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.mapping.entity.CommentMember;
import com.example.Veco.domain.member.entity.Member;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_room_id")
    private CommentRoom commentRoom;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentMember> commentMembers = new ArrayList<>();

    public void setMember(Member member) {
        this.member = member;
    }

    public void setCommentRoom(CommentRoom commentRoom) {
        this.commentRoom = commentRoom;
        commentRoom.getComments().add(this);
    }
}
