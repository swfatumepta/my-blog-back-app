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
        this.validateDataConsistency(postId, commentCreateDto);
        this.checkIfPostExists(postId);

        var commentEntity = new CommentEntity(postId, commentCreateDto.text());
        commentEntity = commentRepository.save(commentEntity);

        var commentDto = this.toCommentDto(commentEntity);
        log.debug("CommentServiceImpl::addPostComment post.id = {}, CreateCommentDto = {} out. Result: {}",
                postId, commentCreateDto, commentDto);
        return commentDto;
    }

    @Override
    @Transactional
    public CommentDto updatePostComment(@NonNull Long postId, @NonNull Long commentId, @NonNull CommentDto updateData) {
        log.debug("CommentServiceImpl::updatePostComment post.id = {}, comment.id = {}, CommentDto = {} in",
                postId, commentId, updateData);
        this.validateDataConsistency(postId, commentId, updateData);
        var updatedEntityData = new CommentEntity(updateData.id(), updateData.postId(), updateData.text());
        var updated = commentRepository.update(updatedEntityData)
                .orElseThrow(() -> {
                    log.error("CommentServiceImpl::updatePostComment post.id = {} with comment.id = {} was not found",
                            postId, commentId);
                    return new CommentNotFoundException(postId, commentId);
                });
        log.debug("CommentServiceImpl::updatePostComment post.id = {}, comment.id = {}, CommentDto = {} out. Result: {}",
                postId, commentId, updateData, updated);
        return updateData;
    }

    private void validateDataConsistency(Long postId, Long commentId, CommentDto commentDto) {
        this.validateDataConsistency(postId, new CommentCreateDto(null, commentDto.postId()));
        if (!commentId.equals(commentDto.id())) {
            log.error("CommentServiceImpl::validateDataConsistency comment.id ({}) != dto.id ({})",
                    postId, commentDto.id());
            throw new InconsistentPostDataException("Request path post.id != dto.postId");
        }
    }

    private void validateDataConsistency(Long postId, CommentCreateDto commentCreateDto) {
        if (!postId.equals(commentCreateDto.postId())) {
            log.error("CommentServiceImpl::validateDataConsistency post.id ({}) != dto.postId ({})",
                    postId, commentCreateDto.postId());
            throw new InconsistentPostDataException("Request path post.id != dto.postId");
        }
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
