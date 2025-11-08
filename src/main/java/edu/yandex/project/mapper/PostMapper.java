package edu.yandex.project.mapper;

import edu.yandex.project.controller.dto.post.PostDto;
import edu.yandex.project.entity.PostEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostMapper {

    @Value("${post.text.size.max:128}")
    private Integer textMaxSize;

    public PostDto toPostDto(PostEntity source) {
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
}
