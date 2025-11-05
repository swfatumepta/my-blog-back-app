package edu.yandex.project.controller;

import edu.yandex.project.controller.dto.post.PostPageDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import edu.yandex.project.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
