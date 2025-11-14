package edu.yandex.project.controller.dto.comment;

import jakarta.validation.constraints.NotNull;

public record CommentCreateDto(@NotNull(message = "CreateCommentDto.text is required")
                               String text,

                               @NotNull(message = "CreateCommentDto.postId is required")
                               Long postId) {
}
