package edu.yandex.project.repository;

import edu.yandex.project.entity.PostEntity;
import org.springframework.lang.NonNull;

import java.util.List;

public interface PostRepository {

    List<PostEntity> findAll(@NonNull String titlePart, int offset, int limit);

    int getPostCount();
}
