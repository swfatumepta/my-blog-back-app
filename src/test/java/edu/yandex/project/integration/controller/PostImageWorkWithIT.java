package edu.yandex.project.integration.controller;

import edu.yandex.project.service.ImageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(executionPhase = BEFORE_TEST_CLASS, scripts = "classpath:sql/controller/post/insert-single-post.sql"),
        @Sql(executionPhase = AFTER_TEST_CLASS, scripts = "classpath:sql/clean-env.sql"),
})
@Tag("Integration tests for PostController - upload/download post image")
public class PostImageWorkWithIT extends AbstractControllerIT {
    private final static String POST_IMAGE_URI = "/api/posts/1/image";
    private final static String TEST_IMAGE_1_LOCATION = "image/test_image.jpg";
    private final static String TEST_IMAGE_2_LOCATION = "image/test_image_2.jpg";

    @Autowired
    private ImageService imageService;

    private Path tempImageDir;

    @BeforeEach
    void initImageDir() throws IOException {
        tempImageDir = Paths.get("src/test/resources/image/to_be_deleted");
        if (Files.exists(tempImageDir)) {
            FileSystemUtils.deleteRecursively(tempImageDir);
        }
        Files.createDirectories(tempImageDir);
    }

    @AfterEach
    void dropImageDir() throws IOException {
        if (Files.exists(tempImageDir)) {
            FileSystemUtils.deleteRecursively(tempImageDir);
        }
    }

    @Test
    void addPostImage_then_getPostImage_success() throws Exception {
        // UPLOAD
        // given
        var testImageResource = new ClassPathResource(TEST_IMAGE_1_LOCATION);
        var testImageFile = testImageResource.getFile();
        var testFileName = testImageFile.getName();

        var uploadingPostImage = testImageResource.getInputStream().readAllBytes();
        var mockFile = new MockMultipartFile(
                "file", testFileName,
                "image/jpeg", uploadingPostImage
        );
        // when
        mockMvc.perform(multipart(HttpMethod.PUT, POST_IMAGE_URI).file(mockFile))
                // then
                .andExpect(status().isOk());

        this.validateUploadedPicture();
        // DOWNLOAD
        // when
        var response = mockMvc.perform(get(POST_IMAGE_URI))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        var downloadedPostImage = response.getResponse().getContentAsByteArray();
        assertArrayEquals(uploadingPostImage, downloadedPostImage);

    }

    @Test
    void addPostImage_inCasePostAlreadyHasImage_imageMustBeRewrite_success() throws Exception {
        // UPLOAD
        // given
        var testImageResource = new ClassPathResource(TEST_IMAGE_1_LOCATION);
        var testFileName = testImageResource.getFile().getName();

        var uploadingPostImage = testImageResource.getInputStream().readAllBytes();
        var postImage = new MockMultipartFile(
                "file", testFileName,
                "image/jpeg", uploadingPostImage
        );
        // when
        mockMvc.perform(multipart(HttpMethod.PUT, POST_IMAGE_URI).file(postImage))
                // then
                .andExpect(status().isOk());

        this.validateUploadedPicture();
        // UPLOAD AGAIN FOR THE SAME POST
        // given
        testImageResource = new ClassPathResource(TEST_IMAGE_2_LOCATION);
        testFileName = testImageResource.getFile().getName();

        uploadingPostImage = testImageResource.getInputStream().readAllBytes();
        postImage = new MockMultipartFile(
                "file", testFileName,
                "image/jpeg", uploadingPostImage
        );
        // when
        mockMvc.perform(multipart(HttpMethod.PUT, POST_IMAGE_URI).file(postImage))
                // then
                .andExpect(status().isOk());

        this.validateUploadedPicture();
        try (var directory = Files.list(tempImageDir)) {
            assertEquals(1, directory.count()); // first image rewrote
        }
    }

    @Test
    void getPostImage_inCasePostImageNotExist_success() throws Exception {
        // given
        // when
        mockMvc.perform(get(POST_IMAGE_URI))
                // then
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[0]));
    }

    private void validateUploadedPicture() {
        var savedImageNamePattern = ReflectionTestUtils.getField(imageService, "imageNamePattern");
        assertNotNull(savedImageNamePattern);
        var savedImageExtension = ReflectionTestUtils.getField(imageService, "imageExtension");
        assertNotNull(savedImageExtension);

        var expectedImageName = MessageFormat.format(savedImageNamePattern.toString(), 1, savedImageExtension);    // Post.id is 1L -> 1
        var expectedSavedImagePath = Paths.get(tempImageDir + "/" + expectedImageName);
        assertTrue(Files.exists(expectedSavedImagePath));
    }
}
