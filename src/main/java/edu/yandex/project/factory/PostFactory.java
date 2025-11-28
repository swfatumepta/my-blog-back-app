package edu.yandex.project.factory;

import edu.yandex.project.controller.dto.post.*;
import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.entity.TagEntity;
import edu.yandex.project.repository.jdbc.util.PostEntityPage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class PostFactory {

    @Getter
    @Value("${post.text.size.max:128}")
    private Integer textMaxSize;

    @Getter
    @Value("${post.text.length.overlimit.stub:...}")
    private String textLengthOverlimitStub;

    public PostPageDto createPostPageDto(@NonNull PostEntityPage source) {
        log.debug("PostMapper::createPostPageDto in");
        var postDtoList = source.getContent().stream()
                .map(this::createPostDto)
                .toList();

        int lastPage = source.getTotalCount() != 0
                ? Math.ceilDiv(source.getTotalCount(), source.getCurrentPageSize()) - 1
                : 0;
        boolean hasNext = source.getTotalCount() > (source.getCurrentPageNumber() + source.getCurrentPageSize());
        boolean hasPrev = source.getCurrentPageNumber() > 0;

        var postPageDto = new PostPageDto(postDtoList, hasNext, hasPrev, lastPage);
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
                    .likesCount(source.getLikesCount())
                    .commentsCount(this.safeGetInt(source.getCommentsCount()))
                    .tags(this.getTagNames(source.getTags()));
        }
        var built = postDtoBuilder.build();
        log.debug("PostMapper::toPostDto {} out. Result: {}", source, built);
        return built;
    }

    private List<String> getTagNames(@Nullable Collection<TagEntity> tagEntities) {
        return tagEntities == null
                ? List.of()
                : tagEntities.stream()
                .map(TagEntity::getName)
                .toList();
    }

    private int safeGetInt(@Nullable Integer integer) {
        return integer != null ? integer : 0;
    }

    private String getPreparedText(String text) {
        var preparedString = text;
        if (text != null && text.length() > 128) {
            preparedString = preparedString.substring(0, textMaxSize) + textLengthOverlimitStub;
        }
        return preparedString;
    }
}
