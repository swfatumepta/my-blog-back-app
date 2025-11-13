package edu.yandex.project.controller;

import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.controller.dto.post.*;
import edu.yandex.project.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts/{postId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getPostComments(@PathVariable Long postId) {
        log.info("CommentController::getPostComments {} begins", postId);
        var response = ResponseEntity.ok(commentService.findPostComments(postId));
        log.info("CommentController::getPostComments {} ends. Result: {}", postId, response.getBody());
        return response;
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getPostComment(@PathVariable Long postId, @PathVariable Long commentId) {
        log.info("CommentController::getPostComment {}: {} begins", postId, commentId);
        var response = ResponseEntity.ok(commentService.findPostComment(postId, commentId));
        log.info("CommentController::getPostComment {}: {} ends. Result: {}", postId, commentId, response.getBody());
        return response;
    }
}
