package edu.yandex.project.integration.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("Integration tests for PostController")
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
}
