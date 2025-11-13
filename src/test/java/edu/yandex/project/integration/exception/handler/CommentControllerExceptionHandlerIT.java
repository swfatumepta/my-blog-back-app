package edu.yandex.project.integration.exception.handler;

import edu.yandex.project.repository.CommentRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("CommentController exception handler integration test")
public class CommentControllerExceptionHandlerIT extends AbstractGlobalExceptionHandlerIT {
    private final static String COMMENTS_ROOT_PATTERN = "/api/posts/{0}/comments";

    @Autowired
    private CommentRepository mockedCommentRepository;

    @Test
    void deletePost_handlePostNotFoundException() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L) + "/5";
        var expectedErrMessage = MessageFormat.format("Post.id = {0} do not have comment.id = {1}", 1, 5);
        // when
        when(mockedCommentRepository.findByPostIdAndCommentId(1L, 5L)).thenReturn(Optional.empty());

        mockMvc.perform(get(uri))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrMessage))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
