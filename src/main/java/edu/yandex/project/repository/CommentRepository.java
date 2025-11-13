package edu.yandex.project.repository;

import edu.yandex.project.entity.CommentEntity;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CommentRepository {

    List<CommentEntity> findByPostId(@NonNull Long postId);
}
