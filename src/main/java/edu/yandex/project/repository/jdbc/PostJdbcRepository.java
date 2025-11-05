package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Repository
public class PostJdbcRepository implements PostRepository {

    private final static Map<Long, PostEntity> DB = Map.of(
            1L, new PostEntity(1L, "title_1", "text_1", 2, 0, LocalDateTime.now()),
            2L, new PostEntity(2L, "title_2", "text_2", 4, 0, LocalDateTime.now())
    );

    @Override
    public List<PostEntity> findAll(@NonNull String titlePart, int offset, int limit) {
        log.debug("PostJdbcRepository::findAll titlePart = {}, offset = {}, limit = {} in", titlePart, offset, limit);
        var fromDb = new ArrayList<>(DB.values());
        log.debug("PostJdbcRepository::findAll titlePart = {}, offset = {}, limit = {} out. Result: {}",
                titlePart, offset, limit, fromDb);
        return fromDb;
    }

    @Override
    public int getPostCount() {
        log.debug("PostJdbcRepository::getPostCount in");
        int count = DB.size();
        log.debug("PostJdbcRepository::getPostCount out. Result: {}", count);
        return count;
    }
}
