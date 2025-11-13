package edu.yandex.project.service.impl;

import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Override
    public List<CommentDto> findPostComments(@NonNull Long postId) {
        log.debug("CommentServiceImpl::findPostComments {} in", postId);
        var commentEntities = List.of();
        var commentDtoList = new ArrayList<CommentDto>();
        log.debug("CommentServiceImpl::findPostComments {} out. Result: {}", postId, commentDtoList);
        return commentDtoList;
    }
}
