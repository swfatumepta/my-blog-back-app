package edu.yandex.project.controller.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public record PostCreateDto(@NotNull(message = "PostCreateDto.title is required")
                            @Size(max = 255, message = "PostCreateDto.title max length = 255")
                            String title,

                            @NotNull(message = "PostCreateDto.text is required")
                            String text) {
// todo
//                            @NotNull(message = "PostCreateDto.tags is required")
//                            List<String> tags
}
