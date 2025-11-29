package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.entity.CommentEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Integration tests for CommentJdbcRepository")
class CommentJdbcRepositoryIT extends AbstractJdbcRepositoryIT {

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
            "1, 1",     // there are no comments and posts in db with given ids
            "777, 1",   // there is single posts with given id in db, but no comments with given id
            "1, 777"    // there is single comment with given id in db, but no post with given id
    })
    @ParameterizedTest
    @Sql("classpath:sql/repository/comment/insert-single-post-with-single-comment.sql")
    void findById_emptyResult_noExceptionThrown(Long postId, Long commentId) {
        // given
        // when
        var actualResult = commentJdbcRepository.findByPostIdAndCommentId(postId, commentId);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @CsvSource({
            "1, 777",   // nonexistent post.id
            "777, 1",   // nonexistent comment.id
            "1, 1",     // nonexistent post.id && nonexistent comment.id
    })
    @ParameterizedTest
    @Sql("classpath:sql/repository/comment/insert-single-post-with-single-comment.sql")
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
                commentJdbcRepository.findByPostIdAndCommentId(DEFAULT_ID, DEFAULT_ID).orElseThrow().getText()
        );
    }

    @CsvSource({
            "1, 777",   // nonexistent post.id
            "777, 1",   // nonexistent comment.id
            "1, 1",     // nonexistent post.id && nonexistent comment.id
    })
    @ParameterizedTest
    @Sql("classpath:sql/repository/comment/insert-single-post-with-single-comment.sql")
    void deleteById_nonExistentPostAndOrComment_dbDataMustNotBeUpdated_and_noExceptionThrown(Long postId, Long commentId) {
        // given
        // when
        int deletedRows = commentJdbcRepository.deleteByPostIdAndCommentId(postId, commentId);
        // then
        assertEquals(0, deletedRows);
    }

    @Sql("classpath:sql/repository/comment/insert-single-post-with-single-comment.sql")
    @Test
    void deleteById_success_existentPostDeletedFromDb() {
        // given
        var toBeDeleted = commentJdbcRepository.findByPostIdAndCommentId(DEFAULT_ID, DEFAULT_ID).orElseThrow();
        // when
        int deletedRows = commentJdbcRepository.deleteByPostIdAndCommentId(toBeDeleted.getPostId(), toBeDeleted.getId());
        // then
        assertEquals(1, deletedRows);
        assertTrue(
                commentJdbcRepository.findByPostIdAndCommentId(toBeDeleted.getPostId(), toBeDeleted.getId()).isEmpty()
        );
    }
}
