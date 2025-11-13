package edu.yandex.project.service;

import edu.yandex.project.controller.dto.comment.CommentDto;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CommentService {

    List<CommentDto> findPostComments(@NonNull Long postId);
}
