package edu.yandex.project.repository.jdbc;

import edu.yandex.project.entity.TagEntity;
import edu.yandex.project.repository.TagRepository;
import edu.yandex.project.repository.jdbc.util.DbUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TagJdbcRepository implements TagRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<TagEntity> createPostTags(@NonNull Long postId, @NonNull List<String> tagsToBeAdded) {
        log.debug("TagJdbcRepository::addPostTags post.id = {}, tags = {} in", postId, tagsToBeAdded);
        List<TagEntity> addedTagEntities = List.of();
        if (!tagsToBeAdded.isEmpty()) {
            var sql = """
                        WITH given_tags(t_name) AS (SELECT UNNEST(:givenTags::text[])),
                        inserted_tags AS (
                            INSERT INTO tags (name)
                                SELECT t_name
                                FROM given_tags
                                ON CONFLICT (name) DO NOTHING
                                RETURNING id, name, created_at
                        ),
                        all_tags AS (
                            SELECT id, name, created_at
                            FROM inserted_tags
                            UNION
                            SELECT t.id, t.name, t.created_at
                            FROM tags t
                                JOIN given_tags gt ON t.name = gt.t_name
                        ),
                        linked_tags AS (
                            INSERT INTO post_tag (post_id, tag_id)
                                SELECT :postId, id FROM all_tags
                                ON CONFLICT (post_id, tag_id) DO NOTHING
                                RETURNING tag_id
                        )
                    SELECT at.id, at.name, at.created_at
                    FROM all_tags at
                    WHERE at.id IN (SELECT tag_id FROM linked_tags)
                    ORDER BY at.name;
                    """;

            var tagsToBeAddedTextArray = DbUtil.convertToNativePostgreSqlTextArray(
                    namedParameterJdbcTemplate.getJdbcTemplate(), tagsToBeAdded
            );
            var namedParameters = Map.of(
                    "postId", postId,
                    "givenTags", tagsToBeAddedTextArray
            );
            addedTagEntities = namedParameterJdbcTemplate.query(sql, namedParameters, new TagEntityRowMapper());
        }
        log.debug("TagJdbcRepository::addPostTags post.id = {}, tags = {} out", postId, addedTagEntities);
        return addedTagEntities;
    }

    @Override
    public List<TagEntity> findAllByPostId(@NonNull Long postId) {
        log.debug("TagJdbcRepository::addPostTags {} in", postId);
        var sql = """
                SELECT t.id, t.name, t.created_at
                FROM tags t
                    LEFT JOIN post_tag pt ON t.id = pt.tag_id
                WHERE pt.post_id = :postId
                """;
        var tagEntities = namedParameterJdbcTemplate.query(sql, Map.of("postId", postId), new TagEntityRowMapper());
        log.debug("TagJdbcRepository::addPostTags {} out. Result: {}", postId, tagEntities);
        return tagEntities;
    }

    @Override
    public void unlinkAllTagsFromPost(@NonNull Long postId) {
        log.debug("TagJdbcRepository::unlinkAllTagsFromPost {} in", postId);
        var sql = "DELETE FROM post_tag WHERE post_id = :postId";
        namedParameterJdbcTemplate.update(sql, Map.of("postId", postId));
        log.debug("TagJdbcRepository::addPostTags {} unlinkAllTagsFromPost out", postId);
    }

    private static class TagEntityRowMapper implements RowMapper<TagEntity> {
        @Override
        public TagEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            log.debug("TagEntityRowMapper::mapRow ResultSet = {}, row = {} in", rs, rowNum);
            var tagEntity = new TagEntity(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );
            log.debug("TagEntityRowMapper::mapRow ResultSet = {}, row = {} out. Result: {}", rs, rowNum, tagEntity);
            return tagEntity;
        }
    }
}
