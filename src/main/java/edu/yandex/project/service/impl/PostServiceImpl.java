package edu.yandex.project.service.impl;

import edu.yandex.project.controller.dto.post.PostPageDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import edu.yandex.project.mapper.PostMapper;
import edu.yandex.project.repository.PostRepository;
import edu.yandex.project.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public PostPageDto findPosts(@NonNull PostPageRequestParameters parameters) {
        log.debug("PostServiceImpl::findPosts {} in", parameters);
        var postEntities = postRepository.findAll(parameters.search(), parameters.pageNumber(), parameters.pageSize());
        int postCount = postRepository.getPostCount();
        // count comment request
        // tags request (?)
        var postDtoList = postEntities.stream()
                .map(postMapper::toPostDto)
                .toList();
        var postPageDto = PostPageDto.builder()
                .posts(postDtoList)
                .hasPrev(parameters.pageNumber() > 0)
                .hasNext(parameters.pageSize() + parameters.pageNumber() < postCount)
                .build();
        log.debug("PostServiceImpl::findPosts {} out. Result: {}", parameters, postPageDto);
        return postPageDto;
    }
}
