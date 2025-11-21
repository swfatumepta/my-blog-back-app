package edu.yandex.project.controller;

import edu.yandex.project.controller.dto.post.*;
import edu.yandex.project.service.PostImageService;
import edu.yandex.project.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostImageService multipartService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<PostPageDto> getPosts(@Valid PostPageRequestParameters requestParameters) {
        log.info("PostController::getPosts {} begins", requestParameters);
        var response = ResponseEntity.ok(postService.findAll(requestParameters));
        log.info("PostController::getPosts {} ends. Result: {}", requestParameters, response.getBody());
        return response;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        log.info("PostController::getPost {} begins", postId);
        var response = ResponseEntity.ok(postService.findOne(postId));
        log.info("PostController::getPost {} ends. Result: {}", postId, response.getBody());
        return response;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody @Valid PostCreateDto postCreateDto) {
        log.info("PostController::createPost {} begins", postCreateDto);
        var response = new ResponseEntity<>(postService.create(postCreateDto), HttpStatus.CREATED);
        log.info("PostController::createPost {} ends. Result: {}", postCreateDto, response.getBody());
        return response;
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId,
                                              @RequestBody @Valid PostUpdateDto postUpdateDto) {
        log.info("PostController::updatePost {} -> {} begins", postId, postUpdateDto);
        var response = ResponseEntity.ok(postService.update(postId, postUpdateDto));
        log.info("PostController::updatePost {} -> {} ends. Result: {}", postId, postUpdateDto, response.getBody());
        return response;
    }

    // почему обновление поста происходит через PUT, а добавление лайка через POST, хотя суть одна - обновление?
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Integer> addPostLike(@PathVariable Long postId) {
        log.info("PostController::addPostLike {} begins", postId);
        var response = ResponseEntity.ok(postService.addLike(postId));
        log.info("PostController::addPostLike {} ends. Result: {}", postId, response.getBody());
        return response;
    }

    // быть может, правильнее было бы возвращать NO_CONTENT?
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        log.info("PostController::deletePost {} begins", postId);
        postService.delete(postId);
        log.info("PostController::deletePost {} ends", postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/images")
    public ResponseEntity<Void> addPostImage(@PathVariable Long postId, @RequestParam("file") MultipartFile file) {
        log.info("PostController::addPostImage {}, {} begins", postId, file);
        multipartService.uploadPostImage(postId, file);
        log.info("PostController::addPostImage {}, {} ends", postId, file);
        return ResponseEntity.ok().build();
    }
}
