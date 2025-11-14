package edu.yandex.project.service;

import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.controller.dto.comment.CommentCreateDto;

import org.springframework.lang.NonNull;

import java.util.List;

public interface CommentService {

    List<CommentDto> findPostComments(@NonNull Long postId);

    CommentDto findPostComment(@NonNull Long postId, @NonNull Long commentId);

    CommentDto addPostComment(@NonNull Long postId, @NonNull CommentCreateDto commentCreateDto);

    CommentDto updatePostComment(@NonNull Long postId, @NonNull Long commentId, @NonNull CommentDto commentDto);
}
