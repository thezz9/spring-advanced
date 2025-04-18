package org.example.expert.common.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {

    /**
     * 예외 코드 (ExceptionCode enum)
     */
    private final ExceptionCode exceptionCode;

    public InvalidRequestException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
