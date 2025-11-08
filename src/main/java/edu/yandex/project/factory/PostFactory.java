package edu.yandex.project.factory;

import edu.yandex.project.controller.dto.post.CreatePostDto;
import edu.yandex.project.controller.dto.post.PostDto;
import edu.yandex.project.controller.dto.post.PostPageDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import edu.yandex.project.entity.PostEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class PostFactory {

    @Value("${post.text.size.max:128}")
    private Integer textMaxSize;

    public PostPageDto createPostPageDto(@NonNull Collection<PostEntity> postEntities,
                                         @NonNull PostPageRequestParameters requestParameters,
                                         @NonNull Long totalPostCount) {
        log.debug("PostMapper::createPostPageDto in");
        var postDtoList = postEntities.stream()
                .map(this::createPostDto)
                .toList();
        var postPageDto = PostPageDto.builder()
                .posts(postDtoList)
                .hasPrev(requestParameters.pageNumber() > 0)
                .hasNext(requestParameters.pageSize() + requestParameters.pageNumber() < totalPostCount)
                .build();
        log.debug("PostMapper::createPostPageDto out");
        return postPageDto;
    }

    public PostDto createPostDto(PostEntity source) {
        log.debug("PostMapper::toPostDto {} in", source);
        var postDtoBuilder = PostDto.builder();
        if (source != null) {
            postDtoBuilder
                    .id(source.getId())
                    .title(source.getTitle())
                    .text(getPreparedText(source.getText()))
                    .likesCount(source.getLikesCount());
        }
        var built = postDtoBuilder.build();
        log.debug("PostMapper::toPostDto {} out. Result: {}", source, built);
        return built;
    }

    private String getPreparedText(String text) {
        var preparedString = text;
        if (text != null && text.length() > 128) {
            preparedString = preparedString.substring(0, textMaxSize - 1) + "...";
        }
        return preparedString;
    }

    public PostEntity createNewPostEntity(@NonNull CreatePostDto createPostDto) {
        log.debug("PostMapper::createPostPageDto {} in", createPostDto);
        var postEntity = PostEntity.builder()
                .title(createPostDto.title())
                .text(createPostDto.text())
                .build();
        log.debug("PostMapper::createPostPageDto {} out", postEntity);
        return postEntity;
    }
}
