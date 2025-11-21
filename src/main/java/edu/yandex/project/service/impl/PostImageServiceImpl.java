package edu.yandex.project.service.impl;

import edu.yandex.project.exception.GeneralProjectException;
import edu.yandex.project.exception.PostNotFoundException;
import edu.yandex.project.repository.PostRepository;
import edu.yandex.project.service.PostImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
public class PostImageServiceImpl implements PostImageService {

    @Value("${file.upload.dir:src/main/resources/uploads/}")
    private String uploadDir;

    @Value("${file.upload.default.extension:.jpg}")
    private String defaultExtension;

    @Value("${file.upload.image.name.pattern:post_id_{0}{1}}")
    private String imageNamePattern;

    private final PostRepository postRepository;

    @Override
    @Transactional
    public void uploadPostImage(@NonNull Long postId, MultipartFile file) {
        log.debug("MultipartServiceImpl::uploadPostImage {}: {} in", postId, this.getFileName(file));
        if (!postRepository.isExistById(postId)) {
            throw new PostNotFoundException(postId);
        }
        if (file != null) {
            try {
                var uploadDirPath = Paths.get(uploadDir);
                log.debug("MultipartServiceImpl::uploadPostImage upload directory path = {}", uploadDirPath.toAbsolutePath());
                if (!Files.exists(uploadDirPath)) {
                    log.debug("MultipartServiceImpl::uploadPostImage dir does not exist, so it will be created..");
                    var createdDir = Files.createDirectories(uploadDirPath);
                    log.debug("MultipartServiceImpl::uploadPostImage upload directory created -> {}", createdDir.toAbsolutePath());
                }
                var generatedFileName = this.generateFileName(postId, file);
                log.debug("MultipartServiceImpl::uploadPostImage file = {} renamed to {}", this.getFileName(file), generatedFileName);
                var filePath = uploadDirPath.resolve(generatedFileName);
                file.transferTo(filePath);
                log.debug("MultipartServiceImpl::uploadPostImage file saved successfully ({})", filePath.toAbsolutePath());
            } catch (IOException exc) {
                log.error("MultipartServiceImpl::uploadPostImage exception thrown while saving file: {}", exc.getLocalizedMessage());
                throw new GeneralProjectException(exc.getLocalizedMessage());
            }
            log.debug("MultipartServiceImpl::uploadPostImage {}: {} out", postId, this.getFileName(file));
        }
    }

    private String generateFileName(Long postId, MultipartFile file) {
        var fileExtension = this.getFileExtension(file.getOriginalFilename());
        return MessageFormat.format(imageNamePattern, postId, fileExtension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return defaultExtension;
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    public String getFileName(@Nullable MultipartFile file) {
        return file != null ? file.getOriginalFilename() : null;
    }

}
