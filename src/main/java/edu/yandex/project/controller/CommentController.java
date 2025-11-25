package edu.yandex.project.controller;

import edu.yandex.project.controller.dto.comment.CommentDto;
import edu.yandex.project.controller.dto.comment.CommentCreateDto;
import edu.yandex.project.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<CommentDto>> getPostComments(@PathVariable("postId") Long postId) {
        log.info("CommentController::getPostComments {} begins", postId);
        var response = ResponseEntity.ok(commentService.findPostComments(postId));
        log.info("CommentController::getPostComments {} ends. Result: {}", postId, response.getBody());
        return response;
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getPostComment(@PathVariable("postId") Long postId,
                                                     @PathVariable("commentId") Long commentId) {
        log.info("CommentController::getPostComment {}: {} begins", postId, commentId);
        var response = ResponseEntity.ok(commentService.findPostComment(postId, commentId));
        log.info("CommentController::getPostComment {}: {} ends. Result: {}", postId, commentId, response.getBody());
        return response;
    }

    @PostMapping
    public ResponseEntity<CommentDto> createPostComment(@PathVariable("postId") Long postId,
                                                        @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("CommentController::createPostComment {}: {} begins", postId, commentCreateDto);
        var response = new ResponseEntity<>(commentService.addPostComment(postId, commentCreateDto), HttpStatus.CREATED);
        log.info("CommentController::createPost {}: {} ends. Result: {}", postId, commentCreateDto, response.getBody());
        return response;
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updatePostComment(@PathVariable("postId") Long postId,
                                                        @PathVariable("commentId") Long commentId,
                                                        @RequestBody @Valid CommentDto commentDto) {
        log.info("CommentController::updatePostComment {}: {}: {} begins", postId, commentId, commentDto);
        var response = ResponseEntity.ok(commentService.updatePostComment(postId, commentId, commentDto));
        log.info("CommentController::updatePostComment {}: {}: {} ends. Result: {}",
                postId, commentId, commentDto, response.getBody());
        return response;
    }

    // быть может, правильнее было бы возвращать NO_CONTENT?
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deletePostComment(@PathVariable("postId") Long postId,
                                                  @PathVariable("commentId") Long commentId) {
        log.info("CommentController::deletePostComment {}: {} begins", postId, commentId);
        commentService.deletePostComment(postId, commentId);
        log.info("CommentController::deletePostComment {}: {} ends", postId, commentId);
        return ResponseEntity.ok().build();
    }
}
