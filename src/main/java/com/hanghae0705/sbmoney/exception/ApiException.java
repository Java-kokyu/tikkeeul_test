package com.hanghae0705.sbmoney.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiException implements BaseExceptionType{

    DUPLICATED_USER(1001, 500, "이미 존재하는 사용자 아이디입니다."),
    NOT_EXIST_USER(1002, 500, "해당 아이디가 없습니다."),
    SHORT_PASSWORD(1003,500,"비밀번호는 4자 이상입니다."),
    SAME_PASSWORD(1004, 500, "유저 이름과 같은 비밀번호는 사용할 수 없습니다."),
    REGEXP_PASSWORD(1005, 500, "비밀번호는 영문 대소문자, 숫자만 사용 가능합니다"),
    NOT_MATCH_USER(1006, 500, "해당 사용자가 아닙니다."),
    NOT_EXIST_DATA(1007, 500, "해당 데이터가 존재하지 않습니다"),
    NOT_VALID_DATA(1008, 400, "잘못된 값이 입력되었습니다."),
    NOT_EXIST_EMAIL(1009, 500, "해당하는 이메일이 없습니다."),
    NOT_EXIST_IN_SECURITY_CONTEXT(2001, 500, "Security Context에 인증 정보가 없습니다."),
    NOT_VALID_TOKEN(3001, 500, "유효한 토큰이 아닙니다."),
    EXPIRED_TOKEN(3002, 500, "만료된 토큰입니다."),
    NO_AUTHORITY_KEY(3003, 500, "권한 정보가 없는 토큰입니다."),
    NOT_VALID_REFRESH_TOKEN(3004, 500, "유효하지 않은 리프레시 토큰입니다"),
    NO_PROVIDER(3010, 500, "provider가 없습니다."),
    NO_COOKIE(4001, 500, "쿠키가 없습니다."),
    NO_CHAT_ROOM(5001, 500, "챗방이 없습니다.");


    private int errorCode;
    private int httpStatus;
    private String errorMessage;

    ApiException(int errorCode, int httpStatus, String errorMessage){
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}
