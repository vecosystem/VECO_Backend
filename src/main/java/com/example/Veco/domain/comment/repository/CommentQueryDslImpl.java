package com.example.Veco.domain.comment.repository;

import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.entity.QComment;
import com.example.Veco.domain.comment.entity.QCommentRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentQueryDslImpl implements CommentQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findByCommentRoomOrderByIdDesc(CommentRoom commentRoom) {
        QComment comment = QComment.comment;

        return queryFactory
                .selectFrom(comment)
                .where(comment.commentRoom.eq(commentRoom)
                        .and(comment.member.deletedAt.isNull()))
                .orderBy(comment.id.desc())
                .fetch();
    }
}
