package edu.yandex.project.controller;

import edu.yandex.project.controller.dto.post.CreatePostDto;
import edu.yandex.project.controller.dto.post.PostDto;
import edu.yandex.project.controller.dto.post.PostPageDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import edu.yandex.project.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PostPageDto> getPosts(@Valid PostPageRequestParameters requestParameters) {
        log.info("PostController::getPosts {} begins", requestParameters);
        var response = ResponseEntity.ok(postService.findPosts(requestParameters));
        log.info("PostController::getPosts {} ends. Result: {}", requestParameters, response.getBody());
        return response;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        log.info("PostController::getPost {} begins", postId);
        var response = ResponseEntity.ok(postService.findPost(postId));
        log.info("PostController::getPost {} ends. Result: {}", postId, response.getBody());
        return response;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody @Valid CreatePostDto createPostDto) {
        log.info("PostController::createPost {} begins", createPostDto);
        var response = new ResponseEntity<>(postService.createPost(createPostDto), HttpStatus.CREATED);
        log.info("PostController::createPost {} ends. Result: {}", createPostDto, response.getBody());
        return response;
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Integer> addPostLike(@PathVariable Long postId) {
        log.info("PostController::addPostLike {} begins", postId);
        var response = ResponseEntity.ok(postService.addLike(postId));
        log.info("PostController::addPostLike {} ends. Result: {}", postId, response.getBody());
        return response;
    }
}
