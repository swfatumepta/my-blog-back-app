package edu.yandex.project.controller.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostUpdateDto(@NotNull(message = "PostUpdateDto.id is required")
                            Long id,

                            @NotNull(message = "PostUpdateDto.title is required")
                            @Size(max = 255, message = "PostUpdateDto.title max length = 255")
                            String title,

                            @NotNull(message = "PostUpdateDto.text is required")
                            String text,

                            @NotNull(message = "PostUpdateDto.tags is required")
                            List<String> tags) {
}
