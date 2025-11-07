package edu.yandex.project.controller.dto.post;

import lombok.Builder;

import java.util.List;

@Builder
public record PostPageDto(List<PostDto> posts, Boolean hasNext, Boolean hasPrev, Integer lastPage) {
}
