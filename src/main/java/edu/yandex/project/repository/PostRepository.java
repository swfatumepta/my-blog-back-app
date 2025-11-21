package edu.yandex.project.repository;

import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.repository.jdbc.util.PostEntityPage;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    PostEntityPage findAll(@NonNull String textFilter, @NonNull List<String> tagsFilter, int offset, int limit);

    Optional<PostEntity> findById(@NonNull Long postId);

    PostEntity save(@NonNull PostEntity toBeSaved);

    Optional<PostEntity> update(@NonNull PostEntity toBeUpdated);

    Optional<Integer> incrementLikesCountById(@NonNull Long postId);

    int deleteById(@NonNull Long postId);

    boolean isExistById(@NonNull Long postId);
}
