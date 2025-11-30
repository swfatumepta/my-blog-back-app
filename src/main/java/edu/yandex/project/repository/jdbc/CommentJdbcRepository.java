package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.CommentEntity;
import edu.yandex.project.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentJdbcRepository implements CommentRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<CommentEntity> findAllByPostId(@NonNull Long postId) {
        log.debug("CommentJdbcRepository::findAllByPostId {} in", postId);
        var sql = """
                SELECT id, text, post_id, created_at
                FROM comments
                WHERE post_id = :postId
                ORDER BY id
                """;
        var fromDb = namedParameterJdbcTemplate.query(sql, Map.of("postId", postId), new CommentEntityRowMapper());
        log.debug("CommentJdbcRepository::findAllByPostId {} out. Result: {}", postId, fromDb);
        return fromDb;
    }

    @Override
    public Optional<CommentEntity> findByPostIdAndCommentId(@NonNull Long postId, @NonNull Long commentId) {
        log.debug("CommentJdbcRepository::findByPostIdAndCommentId post.id = {}, comment.id = {} in", postId, commentId);
        var sql = """
                SELECT id, text, post_id, created_at
                FROM comments
                WHERE post_id = :postId AND id = :commentId
                """;

        CommentEntity fromDb;
        try {
            fromDb = namedParameterJdbcTemplate.queryForObject(
                    sql,
                    Map.of("postId", postId, "commentId", commentId),
                    new CommentEntityRowMapper()
            );
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
                VALUES (:text, :postId)
                RETURNING id, text, post_id, created_at
                """;

        var namedParameters = Map.of(
                "text", toBeSaved.getText(),
                "postId", toBeSaved.getPostId()
        );
        var saved = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new CommentEntityRowMapper());
        log.debug("CommentJdbcRepository::save {} out", saved);
        return saved;
    }

    @Override
    public Optional<CommentEntity> update(@NonNull CommentEntity toBeUpdated) {
        log.debug("CommentJdbcRepository::update {} in", toBeUpdated);
        var sql = """
                UPDATE comments
                SET text = :text
                WHERE id = :commentId AND post_id = :postId
                RETURNING id, text, post_id, created_at
                """;

        var namedParameters = Map.of(
                "text", toBeUpdated.getText(),
                "commentId", toBeUpdated.getId(),
                "postId", toBeUpdated.getPostId()
        );
        CommentEntity updated;
        try {
            updated = namedParameterJdbcTemplate.queryForObject(
                    sql, namedParameters, new CommentEntityRowMapper()
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
        var sql = "DELETE FROM comments WHERE id = :commentId AND post_id = :postId";
        var deletedTotal = namedParameterJdbcTemplate.update(sql, Map.of("postId", postId, "commentId", commentId));
        log.debug("CommentJdbcRepository::deleteByPostIdAndCommentId post.id = {}, comment.id = {} out. Number of deleted rows: {}",
                postId, commentId, deletedTotal);
        return deletedTotal;
    }

    @Override
    public int countPostCommentsTotal(@NonNull Long postId) {
        log.debug("CommentJdbcRepository::countPostCommentsTotal {} in", postId);
        var sql = "SELECT COUNT(id) FROM comments WHERE post_id = :postId";
        var totalCommentsNumber = namedParameterJdbcTemplate.queryForObject(sql, Map.of("postId", postId), Integer.class);
        log.debug("CommentJdbcRepository::countPostCommentsTotal {} out. Result: {}", postId, totalCommentsNumber);
        return totalCommentsNumber != null ? totalCommentsNumber : 0;
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
