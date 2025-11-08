package edu.yandex.project.service;

import edu.yandex.project.controller.dto.post.PostDto;
import edu.yandex.project.controller.dto.post.PostPageDto;
import edu.yandex.project.controller.dto.post.PostPageRequestParameters;
import org.springframework.lang.NonNull;

public interface PostService {

    PostPageDto findPosts(@NonNull PostPageRequestParameters parameters);

    PostDto findPost(@NonNull Long postId);
}
