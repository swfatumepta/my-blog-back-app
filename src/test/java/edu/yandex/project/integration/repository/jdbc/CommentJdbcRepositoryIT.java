package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.entity.CommentEntity;
import edu.yandex.project.repository.jdbc.CommentJdbcRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Tag("Integration tests for CommentJdbcRepository")
class CommentJdbcRepositoryIT extends AbstractJdbcRepositoryIT {

    @Autowired
    private CommentJdbcRepository commentJdbcRepository;

    @Test
    void findAllByPostId_emptyResult_noExceptionThrown() {
        // given
        // table 'posts' is empty
        // when
        var actualResult = commentJdbcRepository.findAllByPostId(1L);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @CsvSource({
            "10, 10",   // there are no comments and posts in db with given ids
            "1, 5",     // there is single posts with given id in db, but no comments with given id
            "5, 1"      // there is single comment with given id in db, but no post with given id
    })
    @ParameterizedTest
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/repository/comment/insert-single-post-with-single-comment.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql")
    })
    void findById_emptyResult_noExceptionThrown(Long postId, Long commentId) {
        // given
        // when
        var actualResult = commentJdbcRepository.findByPostIdAndCommentId(postId, commentId);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @CsvSource({
            "1, 12345678",          // nonexistent post.id
            "12345678, 1",          // nonexistent comment.id
            "12345678, 12345678",   // nonexistent post.id && nonexistent comment.id
    })
    @ParameterizedTest
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/repository/comment/insert-single-post-with-single-comment.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql")
    })
    void update_nonExistentPostAndOrComment_dbDataMustNotBeUpdated_and_noExceptionThrown(Long postId, Long commentId) {
        // given
        var updatedEntityData = new CommentEntity(commentId, postId, LocalDateTime.now().toString());
        // when
        var actualResult = commentJdbcRepository.update(updatedEntityData);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
        assertNotEquals(
                updatedEntityData.getText(),
                commentJdbcRepository.findByPostIdAndCommentId(1L, 1L).orElseThrow().getText()
        );
    }

    @CsvSource({
            "1, 12345678",          // nonexistent post.id
            "12345678, 1",          // nonexistent comment.id
            "12345678, 12345678",   // nonexistent post.id && nonexistent comment.id
    })
    @ParameterizedTest
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/repository/comment/insert-single-post-with-single-comment.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql")
    })
    void deleteById_nonExistentPostAndOrComment_dbDataMustNotBeUpdated_and_noExceptionThrown(Long postId, Long commentId) {
        // given
        // when
        int deletedRows = commentJdbcRepository.deleteByPostIdAndCommentId(postId, commentId);
        // then
        assertEquals(0, deletedRows);
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/repository/comment/insert-single-post-with-single-comment.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql")
    })
    @Test
    void deleteById_success_existentPostDeletedFromDb() {
        // given
        var toBeDeleted = commentJdbcRepository.findByPostIdAndCommentId(1L, 1L).orElseThrow();
        // when
        int deletedRows = commentJdbcRepository.deleteByPostIdAndCommentId(toBeDeleted.getPostId(), toBeDeleted.getId());
        // then
        assertEquals(1, deletedRows);
        assertTrue(
                commentJdbcRepository.findByPostIdAndCommentId(toBeDeleted.getPostId(), toBeDeleted.getId()).isEmpty()
        );
    }
}
