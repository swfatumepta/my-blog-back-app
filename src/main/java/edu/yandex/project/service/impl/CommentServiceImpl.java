package edu.yandex.project.service.impl;

import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.entity.CommentEntity;
import edu.yandex.project.repository.CommentRepository;
import edu.yandex.project.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findPostComments(@NonNull Long postId) {
        log.debug("CommentServiceImpl::findPostComments {} in", postId);
        var commentDtoList = commentRepository.findByPostId(postId).stream()
                .map(this::toCommentDto)
                .toList();
        log.debug("CommentServiceImpl::findPostComments {} out. Result: {}", postId, commentDtoList);
        return commentDtoList;
    }

    private CommentDto toCommentDto(CommentEntity commentEntity) {
        return new CommentDto(commentEntity.getId(), commentEntity.getText(), commentEntity.getPostId());
    }
}
