package com.example.Veco.domain.comment.converter;

import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.global.enums.Category;

public class CommentRoomConverter {

    public static CommentRoom toCommentRoom(Long targetId, Category category) {
        return CommentRoom.builder()
                .targetId(targetId)
                .roomType(category)
                .build();
    }
}
