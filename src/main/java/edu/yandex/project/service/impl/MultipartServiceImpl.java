package edu.yandex.project.service.impl;

import edu.yandex.project.service.MultipartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Service
public class MultipartServiceImpl implements MultipartService {

    @Override
    public void uploadPostImage(@NonNull Long postId, MultipartFile file) {
        log.debug("MultipartServiceImpl::uploadPostImage {}: {} in", postId, getFileName(file));

        log.debug("MultipartServiceImpl::uploadPostImage {}: {} out. Uploaded: {}", postId, getFileName(file), true);
    }

    private static String getFileName(@Nullable MultipartFile file) {
        return file != null ? file.getName() : null;
    }
}
