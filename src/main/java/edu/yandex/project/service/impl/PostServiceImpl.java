package edu.yandex.project.service.impl;

import edu.yandex.project.controller.dto.post.*;
import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.exception.InconsistentPostDataException;
import edu.yandex.project.exception.PostNotFoundException;
import edu.yandex.project.factory.PostFactory;
import edu.yandex.project.repository.CommentRepository;
import edu.yandex.project.repository.PostRepository;
import edu.yandex.project.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final PostFactory postFactory;

    @Override
    @Transactional(readOnly = true)
    public PostPageDto findAll(@NonNull PostPageRequestParameters parameters) {
        log.debug("PostServiceImpl::findAll {} in", parameters);
        int offset = parameters.pageNumber() * parameters.pageSize();
        var postEntityPage = postRepository.findAll(parameters.search(), offset, parameters.pageSize());
        // tags request (?)
        var postPageDto = postFactory.createPostPageDto(postEntityPage);
        log.debug("PostServiceImpl::findAll {} out. Result: {}", parameters, postPageDto);
        return postPageDto;
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto findOne(@NonNull Long postId) {
        log.debug("PostServiceImpl::findOne {} in", postId);
        var postEntity = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("PostServiceImpl::findOne post.id = {} not found", postId);
                    return new PostNotFoundException(postId);
                });
        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::findOne {} out. Result: {}", postId, postEntity);
        return postDto;
    }

    @Override
    @Transactional
    public PostDto create(@NonNull PostCreateDto postCreateDto) {
        log.debug("PostServiceImpl::create {} in", postCreateDto);
        var postEntity = new PostEntity(postCreateDto.title(), postCreateDto.text());

        postEntity = postRepository.save(postEntity);
        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::create {} out", postDto);
        return postDto;
    }

    @Override
    @Transactional
    public PostDto update(@NonNull Long postId, @NonNull PostUpdateDto postUpdateDto) {
        log.debug("PostServiceImpl::update {} -> {} in", postId, postUpdateDto);
        if (!postId.equals(postUpdateDto.id())) {
            log.error("PostServiceImpl::update post.id = {} (path) != post.id = {} (PostUpdateDto.id)",
                    postId, postUpdateDto.id());
            throw new InconsistentPostDataException("Request path post.id != request body post.id");
        }
        var postEntity = new PostEntity(postUpdateDto.id(), postUpdateDto.title(), postUpdateDto.text());

        postEntity = postRepository.update(postEntity)
                .orElseThrow(() -> {
                    log.error("PostServiceImpl::update post.id = {} not found", postId);
                    return new PostNotFoundException(postId);
                });
        int commentsCount = commentRepository.countPostCommentsTotal(postId);
        postEntity.setCommentsCount(commentsCount);

        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::update {} out", postDto);
        return postDto;
    }

    @Override
    @Transactional
    public Integer addLike(@NonNull Long postId) {
        log.debug("PostServiceImpl::addLike {} in", postId);
        var likesCount = postRepository.incrementLikesCountById(postId)
                .orElseThrow(() -> {
                            log.error("PostServiceImpl::addLike post.id = {} not found", postId);
                            return new PostNotFoundException(postId);
                        }
                );
        log.debug("PostServiceImpl::addLike {} out. Total: {}", postId, likesCount);
        return likesCount;
    }

    @Override
    @Transactional
    public void delete(@NonNull Long postId) {
        log.debug("PostServiceImpl::delete {} in", postId);
        int deletedRows = postRepository.deleteById(postId);
        if (deletedRows == 0) {
            log.error("PostServiceImpl::delete post.id = {} not found", postId);
            throw new PostNotFoundException(postId);
        }
        log.debug("PostServiceImpl::delete {} out", postId);
    }
}
