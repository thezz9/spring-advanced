package org.example.expert.common.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    /**
     * 예외 코드 (ExceptionCode enum)
     */
    private final ExceptionCode exceptionCode;

    public AuthException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
