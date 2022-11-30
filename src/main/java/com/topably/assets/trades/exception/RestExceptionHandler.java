package com.topably.assets.trades.exception;

import com.topably.assets.trades.domain.dto.UploadResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MultipartException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<UploadResponseMessage> handleMaxFileSizeException() {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
            .body(new UploadResponseMessage("Unable to upload. File is too large!"));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<UploadResponseMessage> handleMaxSizeException(FileUploadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new UploadResponseMessage(e.getMessage()));
    }
}
