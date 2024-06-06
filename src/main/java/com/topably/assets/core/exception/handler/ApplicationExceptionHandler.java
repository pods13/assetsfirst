package com.topably.assets.core.exception.handler;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.topably.assets.core.exception.ApplicationException;
import com.topably.assets.core.exception.ConstraintsViolationError;
import com.topably.assets.tags.exception.NoSuchTagCategoryException;
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


@Slf4j
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoSuchTagCategoryException.class})
    public ResponseEntity<Object> handleNotFoundExceptions(ApplicationException ex, WebRequest request) {
        return handleExceptionInternal(ex, toBody(ex), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException ex, WebRequest request) {
        return handleExceptionInternal(ex, toBody(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private Object toBody(ApplicationException ex) {
        return toBody(ex, Collections.emptyMap());
    }

    private Object toBody(ApplicationException ex, Map<String, Object> additionalFields) {
        var mainBody = Map.of(
            "message", ex.getMessage(),
            "messageTemplate", ex.getMessageTemplate(),
            "messageArguments", ex.getMessage()
        );
        return Stream.concat(mainBody.entrySet().stream(), additionalFields.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value2));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers,
        HttpStatusCode status, WebRequest request
    ) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new ConstraintsViolationError(e.getField(), e.getDefaultMessage()))
            .toList();
        var exceptionWrapper = new ApplicationException("fields_validation_failed", ex);
        return handleExceptionInternal(ex,
            toBody(exceptionWrapper, Map.of("fieldErrors", fieldErrors)),
            new HttpHeaders(),
            status,
            request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception ex, Object body, HttpHeaders headers,
        HttpStatusCode statusCode, WebRequest request
    ) {
        if (statusCode.is5xxServerError()) {
            log.error("An exception occurred, which will cause a {} response, args: {}", statusCode, body, ex);
        } else if (statusCode.is4xxClientError()) {
            log.warn("An exception occurred, which will cause a {} response, args: {}", statusCode, body, ex);
        } else {
            log.debug("An exception occurred, which will cause a {} response, args: {}", statusCode, body, ex);
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

}
