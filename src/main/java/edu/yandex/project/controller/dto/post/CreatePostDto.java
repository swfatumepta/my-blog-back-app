package edu.yandex.project.controller.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostDto(@NotBlank(message = "CreatePostDto.title is required")
                            @Size(max = 255, message = "Post.title max length = 255")
                            String title,

                            @NotBlank(message = "CreatePostDto.text is required")
                            String text) {
// todo
//                            @NotNull(message = "CreatePostDto.tags is required")
//                            List<String> tags
}
