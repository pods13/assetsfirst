package com.topably.assets.core.exception.handler;

import com.topably.assets.core.exception.ApplicationException;
import com.topably.assets.core.exception.ConstraintsViolationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException ex, WebRequest request) {
        var body = Map.of(
            "message", ex.getMessage()
        );
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new ConstraintsViolationError(e.getField(), e.getDefaultMessage()))
            .toList();
        var responseBody = Map.of(
            "message", ex.getMessage(),
            "errors", fieldErrors
        );
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error("An exception occurred, which will cause a {} response", statusCode, ex);
        } else if (statusCode.is4xxClientError()) {
            log.warn("An exception occurred, which will cause a {} response", statusCode, ex);
        } else {
            log.debug("An exception occurred, which will cause a {} response", statusCode, ex);
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}
