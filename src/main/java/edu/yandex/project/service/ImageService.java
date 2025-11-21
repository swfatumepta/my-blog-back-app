package edu.yandex.project.service;

import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    void upload(@NonNull Long postId, @NonNull MultipartFile file);

    Resource download(@NonNull Long postId);
}
