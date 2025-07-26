package com.example.Veco.domain.comment.dto.request;

import com.example.Veco.global.enums.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateDTO {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
    
    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;
    
    @NotNull(message = "대상 ID는 필수입니다.")
    private Long targetId;
}
