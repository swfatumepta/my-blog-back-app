package edu.yandex.project.controller.dto.post;

import lombok.Builder;

import java.util.List;

@Builder
public record PostDto(Long id,
                      String title,
                      String text,
                      Integer likesCount,
                      Integer commentsCount,
                      List<String> tags) {
}
