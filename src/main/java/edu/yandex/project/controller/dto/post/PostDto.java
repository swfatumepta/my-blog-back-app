package edu.yandex.project.controller.dto.post;

import lombok.Builder;

@Builder
public record PostDto(Long id,
                      String title,
                      String text,
                      Integer likesCount,
                      Integer commentsCount) {
}
