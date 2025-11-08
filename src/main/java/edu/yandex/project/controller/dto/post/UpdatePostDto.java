package edu.yandex.project.controller.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePostDto(@NotNull(message = "UpdatePostDto.id is required")
                            Long id,

                            @NotBlank(message = "UpdatePostDto.title is required")
                            @Size(max = 255, message = "UpdatePostDto.title max length = 255")
                            String title,

                            @NotBlank(message = "UpdatePostDto.text is required")
                            String text) {
// todo
//                            @NotNull(message = "UpdatePostDto.tags is required")
//                            List<String> tags) {
}
