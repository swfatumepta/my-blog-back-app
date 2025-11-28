package edu.yandex.project.integration.exception.handler;

import edu.yandex.project.controller.dto.comment.CommentCreateDto;
import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.exception.handler.ErrorResponse;
import edu.yandex.project.repository.CommentRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("CommentController exception handler integration test")
public class CommentControllerExceptionHandlerIT extends AbstractGlobalExceptionHandlerIT {
    private final static String COMMENTS_ROOT_PATTERN = "/api/posts/{0}/comments";

    @Autowired
    private CommentRepository mockedCommentRepository;

    @MethodSource("commentController_createPostComment_invalidRequestBodyProvider")
    @ParameterizedTest(name = "CommentController::createPostComment -> {0}")
    void createPostComment_handleValidationException(String ignored, TestCaseData testCaseData) throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L);
        // when
        var response = mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((String) testCaseData.testValue()))
                // then
                .andExpect(status().is(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.statusCode").value(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        if (testCaseData.expectedMessage() != null) {
            var errorResponse = OBJECT_MAPPER.readValue(response, ErrorResponse.class);
            assertEquals(testCaseData.expectedMessage, errorResponse.message());
        }
    }

    @Test
    void getPostComment_handlePostNotFoundException() throws Exception {
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

    @Test
    void createPostComment_handleInconsistentPostDataException() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L);
        var requestBody = OBJECT_MAPPER.writeValueAsString(new CommentCreateDto("", 2L));
        // when
        mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                // then
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.message").value("Request path post.id != dto.postId"))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void createPostComment_handlePostNotFoundException() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L);
        var requestBody = OBJECT_MAPPER.writeValueAsString(new CommentCreateDto("", 1L));

        var expectedErrMessage = MessageFormat.format("Post.id = {0} does not exist", 1L);
        // when
        when(mockedPostRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrMessage))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @MethodSource("commentController_updatePostComment_invalidRequestBodyProvider")
    @ParameterizedTest(name = "CommentController::updatePostComment -> {0}")
    void updatePostComment_handleValidationException(String ignored, TestCaseData testCaseData) throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L) + "/1";
        // when
        var response = mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((String) testCaseData.testValue()))
                // then
                .andExpect(status().is(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.statusCode").value(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        if (testCaseData.expectedMessage() != null) {
            var errorResponse = OBJECT_MAPPER.readValue(response, ErrorResponse.class);
            assertEquals(testCaseData.expectedMessage, errorResponse.message());
        }
    }

    @MethodSource("commentController_updatePostComment_inconsistentRequestBodyProvider")
    @ParameterizedTest
    void updatePostComment_handleInconsistentPostDataException(String ignored, TestCaseData testCaseData) throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L) + "/1";
        // when
        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((String) testCaseData.testValue()))
                // then
                .andExpect(status().is(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.statusCode").value(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.message").value(testCaseData.expectedMessage()))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void updatePostComment_handleCommentNotFoundException() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L) + "/1";
        var commentDto = new CommentDto(1L, "", 1L);
        var requestBody = OBJECT_MAPPER.writeValueAsString(commentDto);

        var expectedMessage = MessageFormat.format(
                "Post.id = {0} do not have comment.id = {1}", commentDto.postId(), commentDto.id()
        );
        // when
        when(mockedCommentRepository.update(any())).thenReturn(Optional.empty());

        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void deletePostComment_handleCommentNotFoundException() throws Exception {
        // given
        var uri = MessageFormat.format(COMMENTS_ROOT_PATTERN, 1L) + "/1";
        var expectedMessage = MessageFormat.format("Post.id = {0} do not have comment.id = {1}", 1, 1);
        // when
        when(mockedCommentRepository.deleteByPostIdAndCommentId(1L, 1L)).thenReturn(0);

        mockMvc.perform(delete(uri))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @SneakyThrows
    private static Stream<Arguments> commentController_updatePostComment_inconsistentRequestBodyProvider() {
        return Stream.of(
                Arguments.arguments(
                        "@PathVariable.postId != @RequestBody.postId",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentDto(2L, "", 1L)),
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "Request path comment.id != dto.commentId"
                        )
                ),
                Arguments.arguments(
                        "@PathVariable.commentId != @RequestBody.commentId",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentDto(1L, "", 2L)),
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "Request path post.id != dto.postId"
                        )
                )
        );
    }

    @SneakyThrows
    private static Stream<Arguments> commentController_updatePostComment_invalidRequestBodyProvider() {
        return Stream.of(
                Arguments.arguments(
                        "CommentDto.id is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentDto(null, "", 1L)),
                                HttpStatus.BAD_REQUEST.value(),
                                "CommentDto.id is required"
                        )
                ),
                Arguments.arguments(
                        "CommentDto.text is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentDto(1L, null, 1L)),
                                HttpStatus.BAD_REQUEST.value(),
                                "CommentDto.text is required"
                        )
                ),
                Arguments.arguments(
                        "CommentDto.postId is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentDto(1L, "", null)),
                                HttpStatus.BAD_REQUEST.value(),
                                "CommentDto.postId is required"
                        )
                ),
                Arguments.arguments(
                        "CommentDto all fields are null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentDto(1L, "", null)),
                                HttpStatus.BAD_REQUEST.value(),
                                null
                        )
                )
        );
    }

    @SneakyThrows
    private static Stream<Arguments> commentController_createPostComment_invalidRequestBodyProvider() {
        return Stream.of(
                Arguments.arguments(
                        "CreateCommentDto.text is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentCreateDto(null, 1L)),
                                HttpStatus.BAD_REQUEST.value(),
                                "CreateCommentDto.text is required"
                        )
                ),
                Arguments.arguments(
                        "CreateCommentDto.postId is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentCreateDto("", null)),
                                HttpStatus.BAD_REQUEST.value(),
                                "CreateCommentDto.postId is required"
                        )
                ),
                Arguments.arguments(
                        "CreateCommentDto all fields are null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new CommentCreateDto(null, null)),
                                HttpStatus.BAD_REQUEST.value(),
                                null
                        )
                )
        );
    }

    private record TestCaseData(Object testValue, int expectedStatus, String expectedMessage) {
    }
}
