package edu.yandex.project.controller.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostUpdateDto(@NotNull(message = "PostUpdateDto.id is required")
                            Long id,

                            @NotBlank(message = "PostUpdateDto.title is required")
                            @Size(max = 255, message = "PostUpdateDto.title max length = 255")
                            String title,

                            @NotBlank(message = "PostUpdateDto.text is required")
                            String text) {
// todo
//                            @NotNull(message = "PostUpdateDto.tags is required")
//                            List<String> tags) {
}
