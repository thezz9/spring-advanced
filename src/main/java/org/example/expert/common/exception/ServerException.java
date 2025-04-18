package org.example.expert.common.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    /**
     * 예외 코드 (ExceptionCode enum)
     */
    private final ExceptionCode exceptionCode;

    public ServerException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
