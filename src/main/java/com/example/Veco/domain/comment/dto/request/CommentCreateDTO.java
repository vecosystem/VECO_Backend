package com.example.Veco.domain.comment.dto.request;

import com.example.Veco.global.enums.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateDTO {

    private String content;
    private Category category;
    private Long targetId;
}
