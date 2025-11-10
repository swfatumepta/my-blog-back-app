package edu.yandex.project.exception.handler;

import edu.yandex.project.exception.AbstractProjectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exc,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        log.warn("GlobalExceptionHandler::handleMethodArgumentNotValid {} in", exc.toString());
        var errorMessage = exc.getBindingResult()
                .getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), errorMessage, extractRequestPath(request), LocalDateTime.now()
        );
        log.debug("GlobalExceptionHandler::handleMethodArgumentNotValid {} out", exc.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException exc,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        log.warn("GlobalExceptionHandler::handleHttpMessageNotReadable {} in", exc.toString());
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), exc.getLocalizedMessage(), extractRequestPath(request), LocalDateTime.now()
        );
        log.debug("GlobalExceptionHandler::handleHttpMessageNotReadable {} out", exc.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AbstractProjectException.class)
    protected ResponseEntity<Object> handleAbstractProjectException(AbstractProjectException exc,
                                                                    @NonNull WebRequest request) {
        log.warn("GlobalExceptionHandler::handleAbstractProjectException {} in", exc.toString());
        var errorResponse = new ErrorResponse(
                exc.getHttpStatus().value(), exc.getMessage(), extractRequestPath(request), LocalDateTime.now()
        );
        log.debug("GlobalExceptionHandler::handleAbstractProjectException {} out", exc.toString());
        return new ResponseEntity<>(errorResponse, exc.getHttpStatus());
    }

    private static String extractRequestPath(WebRequest webRequest) {
        return ((ServletWebRequest) webRequest).getRequest().getRequestURI();
    }
}
