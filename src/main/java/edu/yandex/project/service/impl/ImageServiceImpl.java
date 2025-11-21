package edu.yandex.project.service.impl;

import edu.yandex.project.exception.PostImageUploadException;
import edu.yandex.project.exception.PostNotFoundException;
import edu.yandex.project.repository.PostRepository;
import edu.yandex.project.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

@RequiredArgsConstructor
@Slf4j
@Service
public class ImageServiceImpl implements ImageService, InitializingBean {

    @Value("${post.image.dir:src/main/resources/images/}")
    private String imageStoragePath;

    @Value("${post.image.extension:.jpg}")
    private String imageExtension;

    @Value("${post.image.name.pattern:post_id_{0}{1}}")
    private String imageNamePattern;

    private final PostRepository postRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("MultipartServiceImpl::afterPropertiesSet in");
        var uploadPath = Paths.get(imageStoragePath);
        if (!Files.exists(uploadPath)) {
            var createdDir = Files.createDirectories(uploadPath);
            log.debug("MultipartServiceImpl::afterPropertiesSet directory created -> {}", createdDir.toAbsolutePath());
        }
        log.debug("MultipartServiceImpl::afterPropertiesSet out");
    }

    @Override
    @Transactional(readOnly = true)
    public void upload(@NonNull Long postId, @NonNull MultipartFile file) {
        log.debug("MultipartServiceImpl::upload {}: {} in", postId, file);
        this.checkIfPostExists(postId);
        try {
            var postImageName = this.generateFileName(postId);
            var postImagePath = Paths.get(imageStoragePath)
                    .resolve(postImageName);
            file.transferTo(postImagePath);
            log.debug("MultipartServiceImpl::upload {} out. Image saved: {}", postId, postImagePath.toAbsolutePath());
        } catch (IOException exc) {
            log.error("MultipartServiceImpl::upload exception thrown while saving image for post.id = {} -> {}",
                    postId, exc.getLocalizedMessage());
            throw new PostImageUploadException(exc.getLocalizedMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource download(@NonNull Long postId) {
        log.debug("MultipartServiceImpl::download {} in", postId);
        this.checkIfPostExists(postId);

        var expectedImagePath = Paths.get(imageStoragePath, this.generateFileName(postId));
        Resource foundImage = null;
        if (Files.exists(expectedImagePath) && Files.isRegularFile(expectedImagePath)) {
            foundImage = new FileSystemResource(expectedImagePath);
        }
        log.debug("MultipartServiceImpl::download {} out. Result: {}", postId, foundImage);
        return foundImage != null ? foundImage : new ByteArrayResource(new byte[0]);
    }

    private String generateFileName(Long postId) {
        return MessageFormat.format(imageNamePattern, postId, imageExtension);
    }

    private void checkIfPostExists(Long postId) {
        if (!postRepository.isExistById(postId)) {
            log.error("MultipartServiceImpl::checkIfPostExists post.id = {} not found", postId);
            throw new PostNotFoundException(postId);
        }
    }
}
