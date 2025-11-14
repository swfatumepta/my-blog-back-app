package edu.yandex.project.service.impl;

import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.controller.dto.comment.CommentCreateDto;
import edu.yandex.project.entity.CommentEntity;
import edu.yandex.project.exception.CommentNotFoundException;
import edu.yandex.project.exception.InconsistentPostDataException;
import edu.yandex.project.exception.PostNotFoundException;
import edu.yandex.project.repository.CommentRepository;
import edu.yandex.project.repository.PostRepository;
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
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findPostComments(@NonNull Long postId) {
        log.debug("CommentServiceImpl::findPostComments {} in", postId);
        var commentDtoList = commentRepository.findAllByPostId(postId).stream()
                .map(this::toCommentDto)
                .toList();
        log.debug("CommentServiceImpl::findPostComments {} out. Result: {}", postId, commentDtoList);
        return commentDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto findPostComment(@NonNull Long postId, @NonNull Long commentId) {
        log.debug("CommentServiceImpl::findPostComments post.id = {}, comment.id = {} in", postId, commentId);
        var commentEntity = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl::findPostComment comment.id = {} for post.id = {} not found",
                            commentId, postId);
                    return new CommentNotFoundException(postId, commentId);
                });
        var commentDto = this.toCommentDto(commentEntity);
        log.debug("CommentServiceImpl::findPostComments post.id = {}, comment.id = {} out. Result: {}",
                postId, commentId, commentDto);
        return commentDto;
    }

    @Override
    @Transactional
    public CommentDto addPostComment(@NonNull Long postId, @NonNull CommentCreateDto commentCreateDto) {
        log.debug("CommentServiceImpl::addPostComment post.id = {}, CreateCommentDto = {} in", postId, commentCreateDto);
        if (!postId.equals(commentCreateDto.postId())) {
            log.error("CommentServiceImpl::addPostComment post.id ({}) != CreateCommentDto.postId ({})",
                    postId, commentCreateDto.postId());
            throw new InconsistentPostDataException("Request path post.id != comment.postId");
        }
        this.checkIfPostExists(postId);

        var commentEntity = new CommentEntity(postId, commentCreateDto.text());
        commentEntity = commentRepository.save(commentEntity);

        var commentDto = this.toCommentDto(commentEntity);
        log.debug("CommentServiceImpl::addPostComment post.id = {}, CreateCommentDto = {} out. Result: {}",
                postId, commentCreateDto, commentDto);
        return commentDto;
    }

    private void checkIfPostExists(Long postIdToBeChecked) {
        if (postRepository.findById(postIdToBeChecked).isEmpty()) {
            log.error("CommentServiceImpl::checkIfPostExists post.id = {} not found", postIdToBeChecked);
            throw new PostNotFoundException(postIdToBeChecked);
        }
    }

    private CommentDto toCommentDto(CommentEntity commentEntity) {
        return new CommentDto(commentEntity.getId(), commentEntity.getText(), commentEntity.getPostId());
    }
}
