package edu.yandex.project.service.impl;

import edu.yandex.project.controller.dto.post.CreatePostDto;
import edu.yandex.project.controller.dto.post.PostDto;
import edu.yandex.project.controller.dto.post.PostPageDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import edu.yandex.project.exception.PostNotFoundException;
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
    public PostPageDto findPosts(@NonNull PostPageRequestParameters parameters) {
        log.debug("PostServiceImpl::findPosts {} in", parameters);
        var postEntities = postRepository.findAll(parameters.search(), parameters.pageNumber(), parameters.pageSize());
        var postCount = postRepository.getPostCount();
        // count comment request
        // tags request (?)
        var postPageDto = postFactory.createPostPageDto(postEntities, parameters, postCount);
        log.debug("PostServiceImpl::findPosts {} out. Result: {}", parameters, postPageDto);
        return postPageDto;
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto findPost(@NonNull Long postId) {
        log.debug("PostServiceImpl::findPost {} in", postId);
        var postEntity = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::findPost {} out. Result: {}", postId, postEntity);
        return postDto;
    }

    @Override
    @Transactional
    public PostDto createPost(@NonNull CreatePostDto createPostDto) {
        log.debug("PostServiceImpl::createPost {} in", createPostDto);
        var postEntity = postFactory.createNewPostEntity(createPostDto);

        postEntity = postRepository.save(postEntity);
        var postDto = postFactory.createPostDto(postEntity);
        log.debug("PostServiceImpl::createPost {} out", postDto);
        return postDto;
    }
}
