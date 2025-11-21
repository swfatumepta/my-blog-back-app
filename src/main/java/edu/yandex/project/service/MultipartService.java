package edu.yandex.project.service;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

public interface MultipartService {

    void uploadPostImage(@NonNull Long postId, MultipartFile file);
}
