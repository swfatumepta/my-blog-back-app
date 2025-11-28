package edu.yandex.project.integration.controller;

import edu.yandex.project.controller.dto.post.*;
import edu.yandex.project.factory.PostFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("Integration tests for PostController")
public class PostControllerIT extends AbstractControllerIT {
    private final static String POSTS_ROOT = "/api/posts";

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/post/insert-10-posts.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void getPosts_inCaseTwoPagesFound_success() throws Exception {
        // given
        var search = "";     // empty filter -> all posts matches the pattern
        var pageSize = "5";

        var firstPageNo = "1";
        var firstPageRequestParams = Map.of(
                PostPageRequestParameters.Fields.search, search,
                PostPageRequestParameters.Fields.pageNumber, firstPageNo,
                PostPageRequestParameters.Fields.pageSize, pageSize
        );
        var secondPageNo = "2";
        var secondPageRequestParams = Map.of(
                PostPageRequestParameters.Fields.search, search,
                PostPageRequestParameters.Fields.pageNumber, secondPageNo,
                PostPageRequestParameters.Fields.pageSize, pageSize
        );
        // when
        var responseFirstPage = mockMvc.perform(get(POSTS_ROOT).params(MultiValueMap.fromSingleValue(firstPageRequestParams)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(Integer.valueOf(pageSize)))
                .andExpect(jsonPath("$.hasNext").value(true))   // has 2nd page (...1)
                .andExpect(jsonPath("$.hasPrev").value(false))  // from the 1st page (0...)
                .andExpect(jsonPath("$.lastPage").value(1)) // total pages == 2 (0..1)
                .andReturn().getResponse().getContentAsString();
        var parsedFirstPage = objectMapper.readValue(responseFirstPage, PostPageDto.class);
        // when
        var responseSecondPage = mockMvc.perform(get(POSTS_ROOT).params(MultiValueMap.fromSingleValue(secondPageRequestParams)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(Integer.valueOf(pageSize)))
                .andExpect(jsonPath("$.hasNext").value(false))   // last page
                .andExpect(jsonPath("$.hasPrev").value(true))  // has prev page
                .andExpect(jsonPath("$.lastPage").value(1)) // total pages == 2 (0..1)
                .andReturn().getResponse().getContentAsString();
        var parsedSecondPage = objectMapper.readValue(responseSecondPage, PostPageDto.class);
        // validate both results
        var allPostsSet = new HashSet<>(parsedFirstPage.posts());
        allPostsSet.addAll(parsedSecondPage.posts());

        assertEquals(10, allPostsSet.size());
        allPostsSet.forEach(postDto -> Assertions.assertAll(
                () -> assertNotNull(postDto.id()),
                () -> assertTrue(postDto.likesCount() > 0),
                () -> {
                    assertNotNull(postDto.commentsCount());
                    if (postDto.id() == 10) {
                        assertEquals(1, postDto.commentsCount());
                    }
                },
                () -> assertFalse(postDto.title().isEmpty()),
                () -> {
                    assertNotNull(postDto.tags());
                    if (postDto.id() == 8) {
                        assertEquals(2, postDto.tags().size());
                        assertTrue(postDto.tags().containsAll(Set.of("test_tag_1", "test_tag_2")));
                    }
                },

                () -> assertFalse(postDto.text().isEmpty()),
                () -> assertTrue(isValidPostText(postDto.text()))
        ));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/post/insert-10-posts.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void getPosts_inCaseNothingFound_success() throws Exception {
        // given
        var search = "!!!!";    // no matches
        var firstPageNo = "1";
        var pageSize = "5";
        var requestParams = Map.of(
                PostPageRequestParameters.Fields.search, search,
                PostPageRequestParameters.Fields.pageNumber, firstPageNo,
                PostPageRequestParameters.Fields.pageSize, pageSize
        );
        // when
        mockMvc.perform(get(POSTS_ROOT).params(MultiValueMap.fromSingleValue(requestParams)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.lastPage").value(0));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/post/insert-10-posts.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void getPosts_inCaseSinglePostFound_success() throws Exception {
        // given
        var search = "кст длинной 150 сим";    // exactly one match
        var firstPageNo = "1";
        var pageSize = "100";
        var requestParams = Map.of(
                PostPageRequestParameters.Fields.search, search,
                PostPageRequestParameters.Fields.pageNumber, firstPageNo,
                PostPageRequestParameters.Fields.pageSize, pageSize
        );
        // when
        mockMvc.perform(get(POSTS_ROOT).params(MultiValueMap.fromSingleValue(requestParams)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].id").value(10))
                .andExpect(jsonPath("$.posts[0].title").value("Тут лежит текст длинной 150 символов"))
                .andExpect(jsonPath("$.posts[0].text").isNotEmpty())
                .andExpect(jsonPath("$.posts[0].likesCount").value(30))
                .andExpect(jsonPath("$.posts[0].commentsCount").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.lastPage").value(0));
    }

    @Test
    void createPost_success() throws Exception {
        // given
        // check if there are no post in db present
        var requestParams = Map.of(
                PostPageRequestParameters.Fields.search, "",
                PostPageRequestParameters.Fields.pageNumber, "1",
                PostPageRequestParameters.Fields.pageSize, "100"
        );
        mockMvc.perform(get(POSTS_ROOT).params(MultiValueMap.fromSingleValue(requestParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(0));
        // check ends
        var postCreateDto = new PostCreateDto("createPost_success", "void createPost_success() throws Exception", List.of("tag1"));
        var requestBody = objectMapper.writeValueAsString(postCreateDto);
        // when
        var afterCreateResponse = mockMvc.perform(post(POSTS_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(postCreateDto.title()))
                .andExpect(jsonPath("$.text").value(postCreateDto.text()))
                .andExpect(jsonPath("$.likesCount").value(0))   // init value
                .andExpect(jsonPath("$.commentsCount").value(0))   // default value
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags.length()").value(1))
                .andExpect(jsonPath("$.tags[0]").value("tag1"))
                .andReturn().getResponse().getContentAsString();

        var afterCreateParsedResponse = objectMapper.readValue(afterCreateResponse, PostDto.class);
        // and send GET again to make sure, that post added to the DB
        mockMvc.perform(get(POSTS_ROOT).params(MultiValueMap.fromSingleValue(requestParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(1))
                .andExpect(jsonPath("$.posts[0].id").value(afterCreateParsedResponse.id()))
                .andExpect(jsonPath("$.posts[0].title").value(afterCreateParsedResponse.title()))
                .andExpect(jsonPath("$.posts[0].text").value(afterCreateParsedResponse.text()))
                .andExpect(jsonPath("$.posts[0].likesCount").value(afterCreateParsedResponse.likesCount()))
                .andExpect(jsonPath("$.posts[0].commentsCount").value(afterCreateParsedResponse.commentsCount()))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.lastPage").value(0));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/post/insert-single-post.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void updatePost_inCasePostExists_success() throws Exception {
        // given
        var uri = POSTS_ROOT + "/" + 1;

        var responseBeforeUpdate = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Тестовый заголовок 1"))
                .andExpect(jsonPath("$.text").value("Это текст первого тестового поста."))
                .andExpect(jsonPath("$.likesCount").value(42))
                .andExpect(jsonPath("$.commentsCount").value(1))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags.length()").value(2))
                .andReturn().getResponse().getContentAsString();
        var postBeforeUpdate = objectMapper.readValue(responseBeforeUpdate, PostDto.class);
        assertNotNull(postBeforeUpdate);
        assertTrue(postBeforeUpdate.tags().containsAll(Set.of("test_tag_1", "test_tag_2")));

        var postUpdateDto = new PostUpdateDto(
                postBeforeUpdate.id(),
                postBeforeUpdate.title() + " -> TITLE_UPDATED",
                "Совершенно иной текст. Да, и такое бывает!",
                List.of("tag1")
        );
        var requestUpdateBody = objectMapper.writeValueAsString(postUpdateDto);
        // -- preparations finished --
        // when
        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUpdateBody))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postBeforeUpdate.id()))
                .andExpect(jsonPath("$.title").value(postUpdateDto.title()))
                .andExpect(jsonPath("$.text").value(postUpdateDto.text()))
                .andExpect(jsonPath("$.likesCount").value(postBeforeUpdate.likesCount()))
                .andExpect(jsonPath("$.commentsCount").value(1))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags.length()").value(1))
                .andExpect(jsonPath("$.tags[0]").value(postUpdateDto.tags().getFirst()));
        // and send GET to make sure, that post updates committed to the DB
        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postUpdateDto.id()))
                .andExpect(jsonPath("$.title").value(postUpdateDto.title()))
                .andExpect(jsonPath("$.text").value(postUpdateDto.text()))
                .andExpect(jsonPath("$.likesCount").value(postBeforeUpdate.likesCount()))
                .andExpect(jsonPath("$.commentsCount").value(1))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags.length()").value(1))
                .andExpect(jsonPath("$.tags[0]").value(postUpdateDto.tags().getFirst()));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/post/insert-single-post.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void addPostLike_inCasePostExists_success() throws Exception {
        // given
        var uriGET = POSTS_ROOT + "/" + 1;

        var response = mockMvc.perform(get(uriGET))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var postBeforeUpdate = objectMapper.readValue(response, PostDto.class);
        assertNotNull(postBeforeUpdate);

        int expectedLikesCount = postBeforeUpdate.likesCount() + 1;
        var uriLikes = POSTS_ROOT + "/" + postBeforeUpdate.id() + "/likes";
        // -- preparations finished --
        // when
        response = mockMvc.perform(post(uriLikes))
                // then
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(expectedLikesCount, Integer.valueOf(response));
        // and send GET again to make sure, that post updates committed to the DB
        mockMvc.perform(get(uriGET))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postBeforeUpdate.id()))
                .andExpect(jsonPath("$.likesCount").value(expectedLikesCount));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/post/insert-single-post.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void deletePost_inCasePostExists_success() throws Exception {
        // given
        var postIdToBeDeleted = "1";
        var uri = POSTS_ROOT + "/" + postIdToBeDeleted;

        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postIdToBeDeleted));      // check if post exists
        // -- preparations finished --
        // when
        mockMvc.perform(delete(uri))
                // then
                .andExpect(status().isOk());
        // and send GET again to make sure, that delete commited to DB
        mockMvc.perform(get(uri)).andExpect(status().isNotFound());
    }

    private boolean isValidPostText(String postText) {
        int maxPostTextSize = webApplicationContext.getBean(PostFactory.class).getTextMaxSize();
        var textLengthOverlimitStub = webApplicationContext.getBean(PostFactory.class).getTextLengthOverlimitStub();
        boolean hasAllowedLength = postText.length() < maxPostTextSize;
        boolean isCut = postText.contains(textLengthOverlimitStub)
                && (postText.length() == maxPostTextSize + textLengthOverlimitStub.length());

        return hasAllowedLength || isCut;
    }
}
