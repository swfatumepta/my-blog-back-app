package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.repository.jdbc.util.PostEntityPage;
import edu.yandex.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class PostJdbcRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public PostEntityPage findAll(@NonNull String textFragment, int offset, int limit) {
        log.debug("PostJdbcRepository::findAll textFragment = {}, offset = {}, limit = {} in", textFragment, offset, limit);
        var searchPattern = "%" + textFragment + "%";
        var sql = """
                SELECT id, title, text, likes_count, created_at, COUNT(id) OVER() AS total_count
                FROM posts
                WHERE title ILIKE ? OR text ILIKE ?
                ORDER BY created_at
                OFFSET ?
                LIMIT ?
                """;
        var page = jdbcTemplate.query(sql, new PostEntityPageExtractor(), searchPattern, searchPattern, offset, limit);
        if (page == null) {
            page = new PostEntityPage();
        }
        page.setCurrentPageNumber(offset);
        page.setCurrentPageSize(limit);
        log.debug("PostJdbcRepository::findAll textFragment = {}, offset = {}, limit = {} out. Result: {}",
                textFragment, offset, limit, page);
        return page;
    }

    @Override
    public Optional<PostEntity> findById(@NonNull Long postId) {
        log.debug("PostJdbcRepository::findById {} in", postId);
        var sql = """
                SELECT id, title, text, likes_count, created_at
                FROM posts
                WHERE id = ?
                """;
        PostEntity fromDb;
        try {
            fromDb = jdbcTemplate.queryForObject(sql, new PostEntityRowMapper(), postId);
        } catch (EmptyResultDataAccessException exc) {
            fromDb = null;
        }
        log.debug("PostJdbcRepository::findById {} out. Result: {}", postId, fromDb);
        return Optional.ofNullable(fromDb);
    }

    @Override
    public PostEntity save(@NonNull PostEntity toBeSaved) {
        log.debug("PostJdbcRepository::save {} in", toBeSaved);
        var sql = """
                INSERT INTO posts (title, text)
                VALUES (?, ?)
                RETURNING id, title, text, likes_count, created_at
                """;
        var saved = jdbcTemplate.queryForObject(sql, new PostEntityRowMapper(), toBeSaved.getTitle(), toBeSaved.getText());
        log.debug("PostJdbcRepository::save {} out", saved);
        return saved;
    }

    @Override
    public Optional<PostEntity> update(@NonNull PostEntity toBeUpdated) {
        log.debug("PostJdbcRepository::update {} in", toBeUpdated);
        var sql = """
                UPDATE posts
                SET title = ?, text = ?
                WHERE id = ?
                RETURNING id, title, text, likes_count, created_at
                """;
        PostEntity updated;
        try {
            updated = jdbcTemplate.queryForObject(
                    sql, new PostEntityRowMapper(), toBeUpdated.getTitle(), toBeUpdated.getText(), toBeUpdated.getId()
            );
        } catch (EmptyResultDataAccessException exc) {
            updated = null;
        }
        log.debug("PostJdbcRepository::update {} out", updated);
        return Optional.ofNullable(updated);
    }

    @Override
    public Optional<Integer> incrementLikesCountById(@NonNull Long postId) {
        log.debug("PostJdbcRepository::incrementLikesCountById {} in", postId);
        var sql = """
                UPDATE posts
                SET likes_count = likes_count + 1
                WHERE id = ?
                RETURNING likes_count
                """;
        Integer likesTotal;
        try {
            likesTotal = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        } catch (EmptyResultDataAccessException exc) {
            likesTotal = null;
        }
        log.debug("PostJdbcRepository::incrementLikesCountById {} out. Result: {}", postId, likesTotal);
        return Optional.ofNullable(likesTotal);
    }

    @Override
    public int deleteById(@NonNull Long postId) {
        log.debug("PostJdbcRepository::deleteById {} in", postId);
        var sql = "DELETE FROM posts WHERE id = ?";
        var deletedTotal = jdbcTemplate.update(sql, postId);
        log.debug("PostJdbcRepository::deleteById {} out. Number of deleted rows: {}", postId, deletedTotal);
        return deletedTotal;
    }

    private static class PostEntityPageExtractor implements ResultSetExtractor<PostEntityPage> {
        @Override
        public PostEntityPage extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {
            log.debug("PostEntityRowMapper::mapRow ResultSet = {} in", rs);
            var postEntities = new ArrayList<PostEntity>();
            int totalCount = 0;
            while (rs.next()) {
                var postEntity = PostEntity.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .text(rs.getString("text"))
                        .likesCount(rs.getInt("likes_count"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build();
                if (totalCount == 0) {
                    totalCount = rs.getInt("total_count");
                }
                postEntities.add(postEntity);
            }
            var postEntityPage = new PostEntityPage(postEntities, totalCount);
            log.debug("PostEntityRowMapper::mapRow ResultSet = {} out. Result: {}", rs, postEntityPage);
            return postEntityPage;
        }
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
