package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.entity.PostEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Integration tests for PostJdbcJdbcRepository")
class PostJdbcRepositoryIT extends AbstractJdbcRepositoryIT {

    @Test
    void findAll_emptyResult_noExceptionThrown() {
        // given
        // table 'posts' is empty
        // when
        var actualResult = postJdbcRepository.findAll("", List.of(), 0, 100);
        // then
        assertNotNull(actualResult);
        assertNotNull(actualResult.getContent());
        assertTrue(actualResult.getContent().isEmpty());
    }

    @Test
    void findById_emptyResult_noExceptionThrown() {
        // given
        // table 'posts' is empty
        // when
        var actualResult = postJdbcRepository.findById(Long.MIN_VALUE);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void save_success_updatesPostAndReturnAllRequiredFields() {
        // given
        var toBeSaved = new PostEntity("Сказка. Глава 1", "В некотором Царстве, в некотором Государстве..");
        // when
        var actualResult = postJdbcRepository.save(toBeSaved);
        // then
        assertNotNull(actualResult);
        assertAll(
                () -> assertEquals(1L, actualResult.getId()),
                () -> assertEquals(toBeSaved.getTitle(), actualResult.getTitle()),
                () -> assertEquals(toBeSaved.getText(), actualResult.getText()),
                () -> assertEquals(0, actualResult.getLikesCount()),
                () -> assertTrue(LocalDateTime.now().isAfter(actualResult.getCreatedAt()))
        );
    }

    @Test
    void update_nonExistentPost_noExceptionThrown() {
        // given
        var nonExistentPostEntity = new PostEntity(ThreadLocalRandom.current().nextLong(), "", "");
        // when
        var actualResult = postJdbcRepository.update(nonExistentPostEntity);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @Sql("classpath:sql/repository/post/insert-single-post.sql")
    @Test
    void update_successfulUpdate_returnAllRequiredFields() {
        // given
        var toBeUpdated = postJdbcRepository.findById(DEFAULT_ID).orElseThrow();
        var newTitle = "new title";
        var newText = "new text";
        // when
        var afterUpdateReturnValue = postJdbcRepository.update(new PostEntity(toBeUpdated.getId(), newTitle, newText))
                .orElseThrow();
        var afterUpdate = postJdbcRepository.findById(toBeUpdated.getId()).orElseThrow();
        // then
        assertNotNull(afterUpdateReturnValue);
        assertNotNull(afterUpdate);
        // chek if postJdbcRepository.update(..) returns (correct) latest value
        assertAll(
                () -> assertEquals(afterUpdateReturnValue.getId(), afterUpdate.getId()),
                () -> assertEquals(afterUpdateReturnValue.getTitle(), afterUpdate.getTitle()),
                () -> assertEquals(afterUpdateReturnValue.getText(), afterUpdate.getText()),
                () -> assertEquals(afterUpdateReturnValue.getLikesCount(), afterUpdate.getLikesCount()),
                () -> assertEquals(afterUpdateReturnValue.getCreatedAt(), afterUpdate.getCreatedAt())
        );
        // check if update passed correct
        assertAll(
                () -> assertEquals(toBeUpdated.getId(), afterUpdate.getId()),
                () -> assertNotEquals(toBeUpdated.getTitle(), afterUpdate.getTitle()),
                () -> assertNotEquals(toBeUpdated.getText(), afterUpdate.getText()),
                () -> assertEquals(toBeUpdated.getLikesCount(), afterUpdate.getLikesCount()),
                () -> assertEquals(toBeUpdated.getCreatedAt(), afterUpdate.getCreatedAt())
        );
    }

    @Test
    void incrementLikesCountById_nonExistentPost_noExceptionThrown() {
        // given
        // when
        var actualResult = postJdbcRepository.incrementLikesCountById(Long.MIN_VALUE);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @Sql("classpath:sql/repository/post/insert-single-post.sql")
    @Test
    void incrementLikesCountById_success_incrementsLikeAndReturnLatestState() {
        // given
        var toBeUpdated = postJdbcRepository.findById(DEFAULT_ID).orElseThrow();
        // when
        var afterUpdateReturnValue = postJdbcRepository.incrementLikesCountById(toBeUpdated.getId()).orElseThrow();
        var afterUpdate = postJdbcRepository.findById(toBeUpdated.getId()).orElseThrow();
        // then
        assertNotNull(afterUpdateReturnValue);
        assertNotNull(afterUpdate);
        // chek if postJdbcRepository.incrementLikesCountById(..) returns (correct) latest value
        assertEquals(toBeUpdated.getLikesCount() + 1, afterUpdateReturnValue);
        // check if number of likes incremented
        assertEquals(toBeUpdated.getLikesCount() + 1, afterUpdate.getLikesCount());
        // check if postJdbcRepository.incrementLikesCountById(..) didn't touch other fields
        assertAll(
                () -> assertEquals(toBeUpdated.getId(), afterUpdate.getId()),
                () -> assertEquals(toBeUpdated.getTitle(), afterUpdate.getTitle()),
                () -> assertEquals(toBeUpdated.getText(), afterUpdate.getText()),
                () -> assertEquals(toBeUpdated.getCreatedAt(), afterUpdate.getCreatedAt()),
                () -> assertNotEquals(toBeUpdated.getLikesCount(), afterUpdate.getLikesCount())
        );
    }

    @Test
    void deleteById_nonExistentPost_noExceptionThrown() {
        // given
        // when
        int deletedRows = postJdbcRepository.deleteById(Long.MIN_VALUE);
        // then
        assertEquals(0, deletedRows);
    }

    @Sql("classpath:sql/repository/post/insert-single-post.sql")
    @Test
    void deleteById_success_existentPostDeletedFromDb() {
        // given
        var toBeDeleted = postJdbcRepository.findById(DEFAULT_ID).orElseThrow();
        // when
        int deletedRows = postJdbcRepository.deleteById(toBeDeleted.getId());
        // then
        assertEquals(1, deletedRows);
        assertTrue(postJdbcRepository.findById(toBeDeleted.getId()).isEmpty());
    }
}
