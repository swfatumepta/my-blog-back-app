package edu.yandex.project.exception;

import org.springframework.http.HttpStatus;

public class InconsistentPostDataException extends AbstractProjectException {
    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public InconsistentPostDataException(String message) {
        super(DEFAULT_HTTP_STATUS, message);
    }
}
