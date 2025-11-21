package edu.yandex.project.service;

import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

public interface PostImageService {

    void upload(@NonNull Long postId, MultipartFile file);

    Resource download(@NonNull Long postId);
}
