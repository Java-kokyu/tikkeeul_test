package com.hanghae0705.sbmoney.exception;

import lombok.Getter;

public class ApiRuntimeException extends RuntimeException{

    @Getter
    private final BaseExceptionType exceptionType;

    public ApiRuntimeException(BaseExceptionType exceptionType) {
        super(exceptionType.getErrorMessage());
        this.exceptionType = exceptionType;
    }
}
