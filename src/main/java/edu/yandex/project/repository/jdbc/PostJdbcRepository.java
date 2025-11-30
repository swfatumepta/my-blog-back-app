package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.repository.jdbc.util.DbUtil;
import edu.yandex.project.repository.jdbc.util.PostEntityPage;
import edu.yandex.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostJdbcRepository implements PostRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public PostEntityPage findAll(@NonNull String textFilter, @NonNull List<String> tagsFilter, int offset, int limit) {
        log.debug("PostJdbcRepository::findAll textFragment = {}, tagsFilter = {}, offset = {}, limit = {} in",
                textFilter, tagsFilter, offset, limit);
        var sql = """
                    -- CTE with filtration results (by title, text and tags)
                    WITH post_ids_filtered_by_text_and_title AS (
                        SELECT id
                        FROM posts
                        WHERE title ILIKE :title OR text ILIKE :text
                    ),
                    tags_filter AS (
                        SELECT id, name, created_at
                        FROM tags
                        WHERE :ignoreTags OR name = ANY(:tags::text[])
                    ),
                    post_ids_filtered_by_tags AS (
                        SELECT pt.post_id
                        FROM post_tag pt
                            JOIN tags_filter tf ON pt.tag_id = tf.id
                        GROUP BY pt.post_id
                        HAVING :ignoreTags OR COUNT(DISTINCT pt.tag_id) = :tagsCount
                    ),
                    filtered_posts AS (
                        SELECT pifbtat.id
                        FROM post_ids_filtered_by_text_and_title pifbtat
                        WHERE :ignoreTags OR pifbtat.id IN (
                            SELECT pifbt.post_id FROM post_ids_filtered_by_tags pifbt
                        )
                    )
                -- main query
                SELECT p.id AS p_id,
                       p.title AS p_title,
                       p.text AS p_text,
                       p.likes_count AS p_likes,
                       p.created_at AS p_created,
                       COUNT(p.id) OVER() AS p_total_count,
                       COUNT(DISTINCT c.id) AS p_total_comments,
                       COALESCE(
                            JSON_AGG(
                                JSON_BUILD_OBJECT(
                                    'id', t.id,
                                    'name', t.name,
                                    'createdAt', t.created_at
                                )
                            ) FILTER (WHERE t.id IS NOT NULL),
                            '[]'::json
                       ) AS p_tags_json_array
                FROM posts p
                    JOIN filtered_posts fp ON p.id = fp.id
                    LEFT JOIN comments c ON p.id = c.post_id
                    LEFT JOIN post_tag pt ON p.id = pt.post_id
                    LEFT JOIN tags t ON pt.tag_id = t.id
                GROUP BY p.id, p.title, p.text, p.likes_count, p.created_at
                ORDER BY p.created_at DESC
                OFFSET :offset
                LIMIT :limit
                """;

        var namedParameters = this.buildFindAllQueryParameters(textFilter, tagsFilter, offset, limit);
        var postEntityPage = namedParameterJdbcTemplate.query(sql, namedParameters, new PostEntityPageExtractor());

        if (postEntityPage == null) {
            postEntityPage = new PostEntityPage();
        }
        postEntityPage.setCurrentPageNumber(offset);
        postEntityPage.setCurrentPageSize(limit);
        log.debug("PostJdbcRepository::findAll textFragment = {}, tagsFilter = {}, offset = {}, limit = {} out. Result: {}",
                textFilter, tagsFilter, offset, limit, postEntityPage);
        return postEntityPage;
    }

    @Override
    public Optional<PostEntity> findById(@NonNull Long postId) {
        log.debug("PostJdbcRepository::findById {} in", postId);
        var sql = """
                SELECT p.id AS p_id,
                       p.title AS p_title,
                       p.text AS p_text,
                       p.likes_count AS p_likes,
                       p.created_at AS p_created,
                       COUNT(p.id) OVER() AS p_total_count,
                       COUNT(c.id) AS p_total_comments
                FROM posts p
                    LEFT JOIN comments c ON p.id = c.post_id
                WHERE p.id = :postId
                GROUP BY p.id, p.title, p.text, p.likes_count, p.created_at
                ORDER BY p.id
                """;
        PostEntity fromDb;
        try {
            fromDb = namedParameterJdbcTemplate.queryForObject(sql, Map.of("postId", postId), new PostEntityRowMapper());
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
                VALUES (:title, :text)
                RETURNING id p_id, title p_title, text p_text, likes_count p_likes, created_at p_created
                """;
        var saved = namedParameterJdbcTemplate.queryForObject(
                sql, Map.of("title", toBeSaved.getTitle(), "text", toBeSaved.getText()), new PostEntityRowMapper()
        );
        log.debug("PostJdbcRepository::save {} out", saved);
        return saved;
    }

    @Override
    public Optional<PostEntity> update(@NonNull PostEntity toBeUpdated) {
        log.debug("PostJdbcRepository::update {} in", toBeUpdated);
        var sql = """
                UPDATE posts
                SET title = :title, text = :text
                WHERE id = :postId
                RETURNING id p_id, title p_title, text p_text, likes_count p_likes, created_at p_created
                """;

        var namedParameters = Map.of(
                "title", toBeUpdated.getTitle(),
                "text", toBeUpdated.getText(),
                "postId", toBeUpdated.getId()
        );
        PostEntity updated;
        try {
            updated = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new PostEntityRowMapper());
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
                WHERE id = :postId
                RETURNING likes_count
                """;
        Integer likesTotal;
        try {
            likesTotal = namedParameterJdbcTemplate.queryForObject(sql, Map.of("postId", postId), Integer.class);
        } catch (EmptyResultDataAccessException exc) {
            likesTotal = null;
        }
        log.debug("PostJdbcRepository::incrementLikesCountById {} out. Result: {}", postId, likesTotal);
        return Optional.ofNullable(likesTotal);
    }

    @Override
    public int deleteById(@NonNull Long postId) {
        log.debug("PostJdbcRepository::deleteById {} in", postId);
        var sql = "DELETE FROM posts WHERE id = :postId";
        var deletedTotal = namedParameterJdbcTemplate.update(sql, Map.of("postId", postId));
        log.debug("PostJdbcRepository::deleteById {} out. Number of deleted rows: {}", postId, deletedTotal);
        return deletedTotal;
    }

    @Override
    public boolean isExistById(@NonNull Long postId) {
        log.debug("PostJdbcRepository::isExistById {} in", postId);
        var sql = "SELECT EXISTS(SELECT 1 FROM posts WHERE id = :postId)";
        Boolean exists = namedParameterJdbcTemplate.queryForObject(sql, Map.of("postId", postId), Boolean.class);
        log.debug("PostJdbcRepository::isExistById {} out. Result: {}", postId, Boolean.TRUE.equals(exists));
        return Boolean.TRUE.equals(exists);
    }

    private Map<String, Object> buildFindAllQueryParameters(String textFilter,
                                                            List<String> tagsFilter,
                                                            int offset,
                                                            int limit) {
        var textAndTitleSearchPattern = "%" + textFilter + "%";
        var requiredTags = DbUtil.convertToNativePostgreSqlTextArray(
                namedParameterJdbcTemplate.getJdbcTemplate(), tagsFilter
        );
        return Map.of(
                "text", textAndTitleSearchPattern,
                "title", textAndTitleSearchPattern,

                "ignoreTags", tagsFilter.isEmpty(),
                "tagsCount", tagsFilter.size(),
                "tags", requiredTags,

                "offset", offset,
                "limit", limit
        );
    }

    private static class PostEntityPageExtractor implements ResultSetExtractor<PostEntityPage> {
        @Override
        public PostEntityPage extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {
            log.debug("PostEntityRowMapper::mapRow ResultSet = {} in", rs);
            var postEntities = new ArrayList<PostEntity>();
            int totalCount = 0;
            while (rs.next()) {
                var postEntity = PostEntity.builder()
                        .id(rs.getLong("p_id"))
                        .title(rs.getString("p_title"))
                        .text(rs.getString("p_text"))
                        .likesCount(rs.getInt("p_likes"))
                        .commentsCount(rs.getInt("p_total_comments"))
                        .createdAt(rs.getTimestamp("p_created").toLocalDateTime())
                        .tags(new ArrayList<>(DbUtil.getTags(rs)))
                        .build();
                if (totalCount == 0) {
                    totalCount = rs.getInt("p_total_count");
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
                    .id(rs.getLong("p_id"))
                    .title(rs.getString("p_title"))
                    .text(rs.getString("p_text"))
                    .likesCount(rs.getInt("p_likes"))
                    .createdAt(rs.getTimestamp("p_created").toLocalDateTime())
                    .build();
            if (DbUtil.hasColumn(rs, "p_total_comments")) {
                postEntity.setCommentsCount(rs.getInt("p_total_comments"));
            }
            log.debug("PostEntityRowMapper::mapRow ResultSet = {}, row = {} out. Result: {}", rs, rowNum, postEntity);
            return postEntity;
        }
    }
}
