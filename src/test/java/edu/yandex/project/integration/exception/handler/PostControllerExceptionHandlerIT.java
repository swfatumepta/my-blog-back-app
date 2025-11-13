package edu.yandex.project.integration.exception.handler;

import edu.yandex.project.controller.dto.post.PostCreateDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import edu.yandex.project.controller.dto.post.PostUpdateDto;
import edu.yandex.project.exception.handler.ErrorResponse;
import edu.yandex.project.repository.PostRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("PostController exception handler integration test")
public class PostControllerExceptionHandlerIT extends AbstractGlobalExceptionHandlerIT {
    private final static String POSTS_ROOT = "/api/posts";

    @Autowired
    private PostRepository mockedPostRepository;

    @MethodSource("postController_getPosts_invalidRequestParametersProvider")
    @ParameterizedTest(name = "PostController::findPosts -> {0}")
    @SuppressWarnings("unchecked")
    void getPosts_handleValidationException(String ignored, TestCaseData testCaseData) throws Exception {
        // given postController_getPosts_invalidRequestParametersProvider
        // when
        var response = mockMvc.perform(get(POSTS_ROOT)
                        .params(MultiValueMap.fromSingleValue((Map<String, String>) testCaseData.testValue())))
                // then
                .andExpect(status().is(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.statusCode").value(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(POSTS_ROOT))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        if (testCaseData.expectedMessage() != null) {
            var errorResponse = OBJECT_MAPPER.readValue(response, ErrorResponse.class);
            Assertions.assertEquals(testCaseData.expectedMessage, errorResponse.message());
        }
    }

    @MethodSource("postController_createPost_invalidRequestBodyProvider")
    @ParameterizedTest(name = "PostController::createPost -> {0}")
    void createPost_handleValidationException(String ignored, TestCaseData testCaseData) throws Exception {
        // given postController_createPost_invalidRequestBodyProvider
        // when
        var response = mockMvc.perform(post(POSTS_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testCaseData.testValue().toString()))
                // then
                .andExpect(status().is(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.statusCode").value(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(POSTS_ROOT))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        if (testCaseData.expectedMessage() != null) {
            var errorResponse = OBJECT_MAPPER.readValue(response, ErrorResponse.class);
            Assertions.assertEquals(testCaseData.expectedMessage, errorResponse.message());
        }
    }

    @MethodSource("postController_updatePost_invalidRequestBodyProvider")
    @ParameterizedTest(name = "PostController::updatePost -> {0}")
    void updatePost_handleValidationException(String ignored, TestCaseData testCaseData) throws Exception {
        // given postController_updatePost_invalidRequestBodyProvider
        var uri = POSTS_ROOT + "/1";
        // when
        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testCaseData.testValue().toString()))
                // then
                .andExpect(status().is(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.statusCode").value(testCaseData.expectedStatus()))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void updatePost_handleInconsistentPostDataException() throws Exception {
        // given
        var uri = POSTS_ROOT + "/2";
        var requestUpdateBody = OBJECT_MAPPER.writeValueAsString(new PostUpdateDto(1L, "", ""));
        // when
        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUpdateBody))
                // then
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.message").value("Request path post.id != request body post.id"))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void updatePost_handlePostNotFoundException() throws Exception {
        // given
        var uri = POSTS_ROOT + "/1";
        var requestUpdateBody = OBJECT_MAPPER.writeValueAsString(new PostUpdateDto(1L, "", ""));

        var expectedErrMessage = MessageFormat.format("Post.id = {0} does not exist", 1L);
        // when
        when(mockedPostRepository.update(any())).thenReturn(Optional.empty());

        mockMvc.perform(put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUpdateBody))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrMessage))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void addPostLike_handlePostNotFoundException() throws Exception {
        // given
        var uri = POSTS_ROOT + "/1/likes";
        var expectedErrMessage = MessageFormat.format("Post.id = {0} does not exist", 1L);
        // when
        when(mockedPostRepository.incrementLikesCountById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrMessage))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void deletePost_handlePostNotFoundException() throws Exception {
        // given
        var uri = POSTS_ROOT + "/1";
        var expectedErrMessage = MessageFormat.format("Post.id = {0} does not exist", 1L);
        // when
        when(mockedPostRepository.deleteById(1L)).thenReturn(0);

        mockMvc.perform(delete(uri).contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrMessage))
                .andExpect(jsonPath("$.path").value(uri))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @SneakyThrows
    private static Stream<Arguments> postController_updatePost_invalidRequestBodyProvider() {
        return Stream.of(
                Arguments.arguments(
                        "Request body is empty",
                        new TestCaseData("", HttpStatus.BAD_REQUEST.value(), null)
                ),
                Arguments.arguments(
                        "PostUpdateDto.id is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new PostUpdateDto(null, "", "")),
                                HttpStatus.BAD_REQUEST.value(),
                                "PostUpdateDto.id is required"
                        )
                ),
                Arguments.arguments(
                        "PostUpdateDto.title is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new PostUpdateDto(1L, null, "")),
                                HttpStatus.BAD_REQUEST.value(),
                                "PostUpdateDto.title is required"
                        )
                ),
                Arguments.arguments(
                        "PostUpdateDto.title.length > 255",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(
                                        new PostUpdateDto(
                                                1L,
                                                "Spring @ComponentScan: сканирует пакеты на наличие компонентов. Фильтрация по аннотациям, regex, кастомные фильтры. Важно: по умолчанию сканирует текущий пакет! Проблемы: дублирование бинов, конфликты сканирования, медленный старт при большом количестве классов.",
                                                "")
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "PostUpdateDto.title max length = 255"
                        )
                ),
                Arguments.arguments(
                        "PostUpdateDto.text is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new PostUpdateDto(1L, "", null)),
                                HttpStatus.BAD_REQUEST.value(),
                                "PostUpdateDto.text is required"
                        )
                )
        );
    }

    @SneakyThrows
    private static Stream<Arguments> postController_createPost_invalidRequestBodyProvider() {
        return Stream.of(
                Arguments.arguments(
                        "Request body is empty",
                        new TestCaseData("", HttpStatus.BAD_REQUEST.value(), null)
                ),
                Arguments.arguments(
                        "PostCreateDto.title is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new PostCreateDto(null, "")),
                                HttpStatus.BAD_REQUEST.value(),
                                "PostCreateDto.title is required"
                        )
                ),
                Arguments.arguments(
                        "PostCreateDto.title.length > 255",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(
                                        new PostCreateDto(
                                                "Spring @ComponentScan: сканирует пакеты на наличие компонентов. Фильтрация по аннотациям, regex, кастомные фильтры. Важно: по умолчанию сканирует текущий пакет! Проблемы: дублирование бинов, конфликты сканирования, медленный старт при большом количестве классов.",
                                                "")
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "PostCreateDto.title max length = 255"
                        )
                ),
                Arguments.arguments(
                        "PostCreateDto.text is null",
                        new TestCaseData(
                                OBJECT_MAPPER.writeValueAsString(new PostCreateDto("", null)),
                                HttpStatus.BAD_REQUEST.value(),
                                "PostCreateDto.text is required"
                        )
                )
        );
    }

    private static Stream<Arguments> postController_getPosts_invalidRequestParametersProvider() {
        return Stream.of(
                Arguments.arguments(
                        "request parameter 'search' not sent",
                        new TestCaseData(
                                Map.of(
                                        PostPageRequestParameters.Fields.pageNumber, "0",
                                        PostPageRequestParameters.Fields.pageSize, "100"
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "request parameter 'search' is required"
                        )
                ),
                Arguments.arguments(
                        "request parameter 'pageSize' not sent",
                        new TestCaseData(
                                Map.of(
                                        PostPageRequestParameters.Fields.search, "",
                                        PostPageRequestParameters.Fields.pageNumber, "0"
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "request parameter 'pageSize' is required"
                        )
                ),
                Arguments.arguments(
                        "request parameter 'pageNumber' not sent",
                        new TestCaseData(
                                Map.of(
                                        PostPageRequestParameters.Fields.search, "",
                                        PostPageRequestParameters.Fields.pageSize, "100"
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "request parameter 'pageNumber' is required"
                        )
                ),
                Arguments.arguments(
                        "no request parameters sent", new TestCaseData(Map.of(), HttpStatus.BAD_REQUEST.value(), null)
                ),
                Arguments.arguments(
                        "request parameter 'pageNumber' < 0",
                        new TestCaseData(
                                Map.of(
                                        PostPageRequestParameters.Fields.search, "",
                                        PostPageRequestParameters.Fields.pageNumber, "-1",
                                        PostPageRequestParameters.Fields.pageSize, "100"
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "pageNumber must be >= 0"
                        )
                ),
                Arguments.arguments(
                        "request parameter 'pageSize' < 0",
                        new TestCaseData(
                                Map.of(
                                        PostPageRequestParameters.Fields.search, "",
                                        PostPageRequestParameters.Fields.pageNumber, "0",
                                        PostPageRequestParameters.Fields.pageSize, "0"
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "pageSize must be >= 0"
                        )
                ),
                Arguments.arguments(
                        "request parameter 'pageSize' > 100",
                        new TestCaseData(
                                Map.of(
                                        PostPageRequestParameters.Fields.search, "",
                                        PostPageRequestParameters.Fields.pageNumber, "0",
                                        PostPageRequestParameters.Fields.pageSize, "101"
                                ),
                                HttpStatus.BAD_REQUEST.value(),
                                "pageSize must be <= 100"
                        )
                )
        );
    }

    record TestCaseData(Object testValue, int expectedStatus, String expectedMessage) {
    }
}
