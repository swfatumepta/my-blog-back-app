package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.repository.jdbc.CommentJdbcRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

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
}
