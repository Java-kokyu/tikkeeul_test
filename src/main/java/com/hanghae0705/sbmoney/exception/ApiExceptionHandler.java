package com.hanghae0705.sbmoney.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Component
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = { ApiRequestException.class })
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        return new ResponseEntity<>(
                Error.create(e.getExceptionType()), HttpStatus.BAD_REQUEST
        );
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Error{
        private int code;
        private int status;
        private String message;

        static Error create(BaseExceptionType e){
            return new Error(e.getErrorCode(),
                    e.getHttpStatus(),
                    e.getErrorMessage());
        }
    }
}
