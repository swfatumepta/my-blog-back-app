package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.repository.jdbc.TagJdbcRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Tag("Integration tests for TagJdbcRepository")
class TagJdbcRepositoryIT extends AbstractJdbcRepositoryIT {

    @Autowired
    private TagJdbcRepository tagJdbcRepository;

    @Test
    void createPostTags_emptyTagsList_noTagsCreated() {
        // given
        var emptyTagsList = new ArrayList<String>();
        // when
        var actualResult = tagJdbcRepository.createPostTags(1L, emptyTagsList);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/repository/tag/insert-single-post-without-tags.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql")
    })
    @Test
    void createPostTags_newTags_successfullyCreatedAndLinked() {
        // given
        var tagsToBeSavedAndLinkedToThePost = List.of("t1", "t2", "t3");
        // when
        var actualResult = tagJdbcRepository.createPostTags(1L, tagsToBeSavedAndLinkedToThePost);
        // then
        assertNotNull(actualResult);
        assertEquals(tagsToBeSavedAndLinkedToThePost.size(), actualResult.size());

        actualResult.forEach(tag -> {
            assertNotNull(tag.getId());
            assertNotNull(tag.getCreatedAt());
            assertTrue(tagsToBeSavedAndLinkedToThePost.contains(tag.getName()));
        });
        // check if link between saved tags and post committed
        assertEquals(tagsToBeSavedAndLinkedToThePost.size(), tagJdbcRepository.findAllByPostId(1L).size());
    }

    @Test
    void findAllByPostId_emptyResult_noExceptionThrown() {
        // given - table is empty
        // when
        var actualResult = tagJdbcRepository.findAllByPostId(1L);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void findAllByPostId_nonExistentPost_returnsEmptyList() {
        // given
        // when
        var actualResult = tagJdbcRepository.findAllByPostId(Long.MIN_VALUE);
        // then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/repository/tag/insert-single-post-with-tags.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql")
    })
    @Test
    void findAllByPostId_withExistingTags_returnsCorrectTags() {
        // given
        var expectedPostTags = List.of("t1", "t3");
        // when
        var actualResult = tagJdbcRepository.findAllByPostId(1L);
        // then
        assertNotNull(actualResult);
        assertEquals(expectedPostTags.size(), actualResult.size());
        actualResult.forEach(tag -> assertTrue(expectedPostTags.contains(tag.getName())));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/repository/tag/insert-single-post-with-tags.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql")
    })
    @Test
    void unlinkAllTagsFromPost_success_allTagsUnlinked() {
        // given
        var postTagsBeforeUnlink = tagJdbcRepository.findAllByPostId(1L);
        assertFalse(postTagsBeforeUnlink.isEmpty());
        // when
        tagJdbcRepository.unlinkAllTagsFromPost(1L);
        // then
        var postTagsAfterUnlink = tagJdbcRepository.findAllByPostId(1L);
        assertTrue(postTagsAfterUnlink.isEmpty());
    }
}
