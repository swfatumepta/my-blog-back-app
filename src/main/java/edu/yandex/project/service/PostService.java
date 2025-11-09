package edu.yandex.project.service;

import edu.yandex.project.controller.dto.post.*;
import org.springframework.lang.NonNull;

public interface PostService {

    PostPageDto findAll(@NonNull PostPageRequestParameters parameters);

    PostDto findOne(@NonNull Long postId);

    PostDto create(@NonNull PostCreateDto postCreateDto);

    PostDto update(@NonNull Long postId, @NonNull PostUpdateDto postUpdateDto);

    Integer addLike(@NonNull Long postId);

    void delete(@NonNull Long postId);
}
