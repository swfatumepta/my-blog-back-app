package edu.yandex.project.service.impl;

import edu.yandex.project.exception.GeneralProjectException;
import edu.yandex.project.exception.PostNotFoundException;
import edu.yandex.project.repository.PostRepository;
import edu.yandex.project.service.PostImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    @Transactional(readOnly = true)
    public void upload(@NonNull Long postId, MultipartFile file) {
        log.debug("MultipartServiceImpl::upload {}: {} in", postId, this.getFileName(file));
        this.checkIfPostExists(postId);
        if (file != null) {
            try {
                var uploadDirPath = Paths.get(uploadDir);
                log.debug("MultipartServiceImpl::upload directory path = {}", uploadDirPath.toAbsolutePath());
                if (!Files.exists(uploadDirPath)) {
                    log.debug("MultipartServiceImpl::upload dir does not exist, so it will be created..");
                    var createdDir = Files.createDirectories(uploadDirPath);
                    log.debug("MultipartServiceImpl::upload directory created -> {}", createdDir.toAbsolutePath());
                }
                var generatedFileName = this.generateFileName(postId, file);
                log.debug("MultipartServiceImpl::upload file = {} renamed to {}", this.getFileName(file), generatedFileName);
                var filePath = uploadDirPath.resolve(generatedFileName);
                file.transferTo(filePath);
                log.debug("MultipartServiceImpl::upload file saved successfully ({})", filePath.toAbsolutePath());
            } catch (IOException exc) {
                log.error("MultipartServiceImpl::upload exception thrown while saving file: {}", exc.getLocalizedMessage());
                throw new GeneralProjectException(exc.getLocalizedMessage());
            }
            log.debug("MultipartServiceImpl::upload {}: {} out", postId, this.getFileName(file));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource download(@NonNull Long postId) {
        log.debug("MultipartServiceImpl::download {} in", postId);
        this.checkIfPostExists(postId);
        var downloadDirPath = Paths.get(uploadDir);
        log.debug("MultipartServiceImpl::download directory path = {}", downloadDirPath.toAbsolutePath());
        Resource found = null;
        if (!Files.exists(downloadDirPath)) {
            log.debug("MultipartServiceImpl::download dir does not exist");
            found = new ByteArrayResource(new byte[]{});
        } else {
            try (var filesStream = Files.list(downloadDirPath)) {
                var expectedImageName = MessageFormat.format(imageNamePattern, postId, defaultExtension);
                var filePath = filesStream.filter(path -> {
                            var filename = path.getFileName().toString();
                            return filename.equals(expectedImageName);
                        })
                        .findFirst();
                if (filePath.isPresent()) {
                    var content = Files.readAllBytes(filePath.get());
                    found = new ByteArrayResource(content);
                }
            } catch (IOException exc) {
                log.error("MultipartServiceImpl::download exception thrown while downloading file: {}", exc.getLocalizedMessage());
                throw new GeneralProjectException(exc.getLocalizedMessage());
            }
        }
        log.debug("MultipartServiceImpl::download {} out. Result: {}", postId, found);
        return found;
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

    private void checkIfPostExists(Long postId) {
        if (!postRepository.isExistById(postId)) {
            throw new PostNotFoundException(postId);
        }
    }
}
