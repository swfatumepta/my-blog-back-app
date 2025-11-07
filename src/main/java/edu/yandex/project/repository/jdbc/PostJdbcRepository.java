package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class PostJdbcRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<PostEntity> findAll(@NonNull String textFragment, int offset, int limit) {
        log.debug("PostJdbcRepository::findAll textFragment = {}, offset = {}, limit = {} in", textFragment, offset, limit);
        var searchPattern = "%" + textFragment + "%";
        var sql = """
                SELECT id, title, text, likes_count, created_at
                FROM posts
                WHERE title ILIKE ? OR text ILIKE ?
                ORDER BY created_at
                OFFSET ?
                LIMIT ?
                """;
        var fromDb = jdbcTemplate.query(sql, new PostEntityRowMapper(), searchPattern, searchPattern, offset, limit);
        log.debug("PostJdbcRepository::findAll textFragment = {}, offset = {}, limit = {} out. Result: {}",
                textFragment, offset, limit, fromDb);
        return fromDb;
    }

    @Override
    public Long getPostCount() {
        log.debug("PostJdbcRepository::getPostCount in");
        var sql = "SELECT COUNT(id) FROM posts";
        var count = jdbcTemplate.queryForObject(sql, Long.class);
        log.debug("PostJdbcRepository::getPostCount out. Result: {}", count);
        return count;
    }

    private static class PostEntityRowMapper implements RowMapper<PostEntity> {
        @Override
        public PostEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            log.debug("PostEntityRowMapper::mapRow ResultSet = {}, row = {} in", rs, rowNum);
            var postEntity = PostEntity.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .text(rs.getString("text"))
                    .likesCount(rs.getInt("likes_count"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();
            log.debug("PostEntityRowMapper::mapRow ResultSet = {}, row = {} out. Result: {}", rs, rowNum, postEntity);
            return postEntity;
        }
    }
}
