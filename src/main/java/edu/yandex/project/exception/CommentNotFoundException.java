package edu.yandex.project.exception;

import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

public class CommentNotFoundException extends AbstractProjectException {
    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;
    private final static String ERROR_MESSAGE_PATTERN = "Post.id = {0} do not have comment.id = {1}";

    public CommentNotFoundException(Long postId, Long commentId) {
        super(DEFAULT_HTTP_STATUS, MessageFormat.format(ERROR_MESSAGE_PATTERN, postId, commentId));
    }
}
