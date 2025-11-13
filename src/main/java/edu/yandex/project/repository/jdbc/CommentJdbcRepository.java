package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.CommentEntity;
import edu.yandex.project.repository.CommentRepository;
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
public class CommentJdbcRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<CommentEntity> findByPostId(@NonNull Long postId) {
        log.debug("CommentJdbcRepository::findByPostId {} in", postId);
        var sql = """
                SELECT id, text, post_id, created_at
                FROM comments
                WHERE post_id = ?
                ORDER BY id
                """;
        var fromDb = jdbcTemplate.query(sql, new CommentEntityRowMapper(), postId);
        log.debug("CommentJdbcRepository::findByPostId {} out. Result: {}", postId, fromDb);
        return fromDb;
    }

    private static class CommentEntityRowMapper implements RowMapper<CommentEntity> {
        @Override
        public CommentEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            log.debug("CommentEntityRowMapper::mapRow ResultSet = {}, row = {} in", rs, rowNum);
            var postEntity = CommentEntity.builder()
                    .id(rs.getLong("id"))
                    .postId(rs.getLong("post_id"))
                    .text(rs.getString("text"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();
            log.debug("CommentEntityRowMapper::mapRow ResultSet = {}, row = {} out. Result: {}", rs, rowNum, postEntity);
            return postEntity;
        }
    }
}
