package edu.yandex.project.controller.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@FieldNameConstants
public record CommentDto(@NotNull(message = "CommentDto.id is required")
                         Long id,

                         @NotNull(message = "CommentDto.text is required")
                         String text,

                         @NotNull(message = "CommentDto.postId is required")
                         Long postId) {
}
