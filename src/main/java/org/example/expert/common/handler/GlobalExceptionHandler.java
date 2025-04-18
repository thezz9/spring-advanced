package org.example.expert.common.handler;

import org.example.expert.common.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ExceptionResponseDto> invalidRequestExceptionException(InvalidRequestException ex) {
        return ExceptionResponseDto.dtoResponseEntity(ex.getExceptionCode());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponseDto> handleAuthException(AuthException ex) {
        return ExceptionResponseDto.dtoResponseEntity(ex.getExceptionCode());
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponseDto> handleServerException(ServerException ex) {
        return ExceptionResponseDto.dtoResponseEntity(ex.getExceptionCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        ExceptionCode code = ExceptionCode.VALIDATION_FAILED;

        return ResponseEntity.status(code.getStatus().value())
                .body(ExceptionResponseDto.builder()
                        .status(code.getStatus().value())
                        .code(code.getCode())
                        .message(defaultMessage)
                        .build());
    }

}

