package edu.yandex.project.repository;

import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.entity.CommentEntity;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    List<CommentEntity> findAllByPostId(@NonNull Long postId);

    Optional<CommentEntity> findByPostIdAndCommentId(@NonNull Long postId, @NonNull Long commentId);

    CommentEntity save(@NonNull CommentEntity toBeSaved);

    Optional<CommentEntity> update(@NonNull CommentEntity toBeUpdated);
}
