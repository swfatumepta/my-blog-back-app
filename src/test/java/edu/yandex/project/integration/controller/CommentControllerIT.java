package edu.yandex.project.integration.controller;

import edu.yandex.project.controller.dto.comment.CommentCreateDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.text.MessageFormat;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("Integration tests for CommentController")
public class CommentControllerIT extends AbstractControllerIT {
    private final static String COMMENTS_ROOT_PATTERN = "/api/posts/{0}/comments";

    @Test
    void getPostComments_inCasePostHasNoComments_success() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L);
        // when
        mockMvc.perform(get(uri))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/comment/insert-single-post-with-5-comments.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void getPostComments_inCasePostHas5Comments_mustBeSortedById_success() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L);
        // when
        mockMvc.perform(get(uri))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Это первый комментарий к тестовому посту."))
                .andExpect(jsonPath("$[0].postId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("Отличная статья! Спасибо за информацию."))
                .andExpect(jsonPath("$[1].postId").value(1));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/comment/insert-single-post-with-5-comments.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void getPostComment_inCasePostWithItsCommentExist_success() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L) + "/5";
        // when
        mockMvc.perform(get(uri))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.text").value("Буду ждать продолжения."))
                .andExpect(jsonPath("$.postId").value(1));
    }

    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "classpath:sql/controller/comment/insert-single-post-with-5-comments.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:sql/clean-env.sql"),
    })
    @Test
    void createPostComment_inCasePostExists_success() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L);

        var commentCreateDto = new CommentCreateDto("самый новый коментарий, новее нет", 1L);
        var requestBody = objectMapper.writeValueAsString(commentCreateDto);
        // when
        mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.text").value(commentCreateDto.text()))
                .andExpect(jsonPath("$.postId").value(commentCreateDto.postId()));
    }
}
