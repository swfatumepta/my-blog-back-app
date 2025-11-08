package edu.yandex.project.service.impl;

import edu.yandex.project.controller.dto.post.*;
import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.exception.InconsistentPostDataException;
import edu.yandex.project.exception.PostNotFoundException;
import edu.yandex.project.exception.ProjectException;
import edu.yandex.project.factory.PostFactory;
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

    private final PostRepository postRepository;
    private final PostFactory postFactory;

    @Override
    @Transactional(readOnly = true)
    public PostPageDto findAll(@NonNull PostPageRequestParameters parameters) {
        log.debug("PostServiceImpl::findAll {} in", parameters);
        var postEntities = postRepository.findAll(parameters.search(), parameters.pageNumber(), parameters.pageSize());
        var postCount = postRepository.getPostCount();
        // count comment request
        // tags request (?)
        var postPageDto = postFactory.createPostPageDto(postEntities, parameters, postCount);
        log.debug("PostServiceImpl::findAll {} out. Result: {}", parameters, postPageDto);
        return postPageDto;
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto findOne(@NonNull Long postId) {
        log.debug("PostServiceImpl::findOne {} in", postId);
        var postEntity = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::findOne {} out. Result: {}", postId, postEntity);
        return postDto;
    }

    @Override
    @Transactional
    public PostDto create(@NonNull CreatePostDto createPostDto) {
        log.debug("PostServiceImpl::create {} in", createPostDto);
        var postEntity = new PostEntity(createPostDto.title(), createPostDto.text());

        postEntity = postRepository.save(postEntity);
        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::create {} out", postDto);
        return postDto;
    }

    @Override
    @Transactional
    public PostDto update(@NonNull Long postId, @NonNull UpdatePostDto updatePostDto) {
        log.debug("PostServiceImpl::update {} -> {} in", postId, updatePostDto);
        if (!postId.equals(updatePostDto.id())) {
            throw new InconsistentPostDataException();
        }
        var postEntity = new PostEntity(updatePostDto.id(), updatePostDto.title(), updatePostDto.text());

        postEntity = postRepository.update(postEntity).orElseThrow(PostNotFoundException::new);
        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::update {} out", postDto);
        return postDto;
    }

    @Override
    @Transactional
    public Integer addLike(@NonNull Long postId) {
        log.debug("PostServiceImpl::addLike {} in", postId);
        var likesCount = postRepository.incrementLikesCountById(postId).orElseThrow(PostNotFoundException::new);
        log.debug("PostServiceImpl::addLike {} out. Total: {}", postId, likesCount);
        return likesCount;
    }

    @Override
    @Transactional
    public void delete(@NonNull Long postId) {
        log.debug("PostServiceImpl::delete {} in", postId);
        int deletedRows = postRepository.deleteById(postId);
        if (deletedRows == 0) {
            throw new PostNotFoundException();
        } else if (deletedRows > 1) {
            throw new ProjectException();
        }
        log.debug("PostServiceImpl::delete {} out", postId);
    }
}
