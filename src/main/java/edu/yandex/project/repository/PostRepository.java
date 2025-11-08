package edu.yandex.project.repository;

import edu.yandex.project.entity.PostEntity;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    List<PostEntity> findAll(@NonNull String titlePart, int offset, int limit);

    Long getPostCount();

    Optional<PostEntity> findById(@NonNull Long postId);

    PostEntity save(@NonNull PostEntity postEntity);

    Optional<Integer> incrementLikesCountById(@NonNull Long postId);

    int deleteById(@NonNull Long postId);
}
