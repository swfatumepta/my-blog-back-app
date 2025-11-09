package edu.yandex.project.exception;

import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

public class PostNotFoundException extends AbstractProjectException {
    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;
    private final static String ERROR_MESSAGE_PATTERN = "Post.id = {0} does not exist";

    public PostNotFoundException(Long postId) {
        super(DEFAULT_HTTP_STATUS, MessageFormat.format(ERROR_MESSAGE_PATTERN, postId));
    }
}
