package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.CommentEntity;
import edu.yandex.project.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CommentJdbcRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<CommentEntity> findAllByPostId(@NonNull Long postId) {
        log.debug("CommentJdbcRepository::findAllByPostId {} in", postId);
        var sql = """
                SELECT id, text, post_id, created_at
                FROM comments
                WHERE post_id = ?
                ORDER BY id
                """;
        var fromDb = jdbcTemplate.query(sql, new CommentEntityRowMapper(), postId);
        log.debug("CommentJdbcRepository::findAllByPostId {} out. Result: {}", postId, fromDb);
        return fromDb;
    }

    @Override
    public Optional<CommentEntity> findByPostIdAndCommentId(@NonNull Long postId, @NonNull Long commentId) {
        log.debug("CommentJdbcRepository::findByPostIdAndCommentId post.id = {}, comment.id = {} in", postId, commentId);
        var sql = """
                SELECT id, text, post_id, created_at
                FROM comments
                WHERE post_id = ? AND id = ?
                """;
        CommentEntity fromDb;
        try {
            fromDb = jdbcTemplate.queryForObject(sql, new CommentEntityRowMapper(), postId, commentId);
        } catch (EmptyResultDataAccessException exc) {
            fromDb = null;
        }
        log.debug("CommentJdbcRepository::findByPostIdAndCommentId post.id = {}, comment.id = {} out. Result: {}",
                postId, commentId, fromDb);
        return Optional.ofNullable(fromDb);
    }

    @Override
    public CommentEntity save(@NonNull CommentEntity toBeSaved) {
        log.debug("CommentJdbcRepository::save {} in", toBeSaved);
        var sql = """
                INSERT INTO comments (text, post_id)
                VALUES (?, ?)
                RETURNING id, text, post_id, created_at
                """;
        var saved = jdbcTemplate.queryForObject(sql, new CommentEntityRowMapper(), toBeSaved.getText(), toBeSaved.getPostId());
        log.debug("CommentJdbcRepository::save {} out", saved);
        return saved;
    }

    @Override
    public Optional<CommentEntity> update(@NonNull CommentEntity toBeUpdated) {
        log.debug("CommentJdbcRepository::update {} in", toBeUpdated);
        var sql = """
                UPDATE comments
                SET text = ?
                WHERE id = ? AND post_id = ?
                RETURNING id, text, post_id, created_at
                """;
        CommentEntity updated;
        try {
            updated = jdbcTemplate.queryForObject(
                    sql, new CommentEntityRowMapper(), toBeUpdated.getText(), toBeUpdated.getId(), toBeUpdated.getPostId()
            );
        } catch (EmptyResultDataAccessException exc) {
            updated = null;
        }
        log.debug("CommentJdbcRepository::update {} out", updated);
        return Optional.ofNullable(updated);
    }

    @Override
    public int deleteByPostIdAndCommentId(@NonNull Long postId, @NonNull Long commentId) {
        log.debug("CommentJdbcRepository::deleteByPostIdAndCommentId post.id = {}, comment.id = {} in", postId, commentId);
        var sql = "DELETE FROM comments WHERE id = ? AND post_id = ?";
        var deletedTotal = jdbcTemplate.update(sql, commentId, postId);
        log.debug("CommentJdbcRepository::deleteByPostIdAndCommentId post.id = {}, comment.id = {} out. Number of deleted rows: {}",
                postId, commentId, deletedTotal);
        return deletedTotal;
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
