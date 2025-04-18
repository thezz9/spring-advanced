package org.example.expert.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 예외 코드 정의 enum
 */
@Getter
@AllArgsConstructor
public enum ExceptionCode {

    /**
     * Auth
     */
    NOT_LOGGED_IN(HttpStatus.UNAUTHORIZED, "NOT_LOGGED_IN", "로그인되지 않은 사용자입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    INVALID_AUTH_CONFIGURATION(HttpStatus.INTERNAL_SERVER_ERROR, "INVALID_AUTH_CONFIGURATION", "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    INVALID_AUTH_HEADER(HttpStatus.BAD_REQUEST, "INVALID_AUTH_HEADER", "유효하지 않은 헤더 형식입니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "NOT_FOUND_TOKEN", "토큰이 존재하지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_ACCESS_TOKEN", "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),

    /**
     * User
     */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 가입된 이메일입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NOT_FOUND_USER", "사용자가 존재하지 않습니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "SAME_AS_OLD_PASSWORD", "기존 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "INVALID_USER_ROLE", "유효하지 않은 UserRole 입니다."),

    /**
     * Todo
     */
    NOT_FOUND_TODO(HttpStatus.NOT_FOUND, "NOT_FOUND_TODO", "일정이 존재하지 않습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "FORBIDDEN_ACCESS","접근 권한이 없습니다."),

    /**
     * Comment
     */
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "NOT_FOUND_COMMENT", "댓글이 존재하지 않습니다."),

    /**
     * Manager
     */
    INVALID_WRITER_USER(HttpStatus.BAD_REQUEST, "INVALID_WRITER_USER", "일정을 만든 유저가 유효하지 않습니다."),
    MANAGER_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "MANAGER_USER_NOT_FOUND", "등록하려는 담당자 유저가 존재하지 않습니다."),
    CANNOT_ASSIGN_SELF_AS_MANAGER(HttpStatus.BAD_REQUEST, "CANNOT_ASSIGN_SELF_AS_MANAGER", "일정 작성자는 본인을 담당자로 등록할 수 없습니다."),
    NOT_ASSIGNED_TO_TODO(HttpStatus.FORBIDDEN, "NOT_ASSIGNED_TO_TODO", "해당 일정에 등록된 담당자가 아닙니다."),

    /**
     * Global
     */
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACCESS","접근할 수 없는 사용자입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED","입력 값이 유효하지 않습니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_DATE_FORMAT","올바른 날짜 형식이 아닙니다."),
    UPDATE_FAILED(HttpStatus.NOT_FOUND, "UPDATE_FAILED","데이터 변경에 실패했습니다."),
    DELETE_FAILED(HttpStatus.NOT_FOUND, "DELETE_FAILED","데이터 삭제에 실패했습니다."),
    NO_CHANGES(HttpStatus.NO_CONTENT, "NO_CHANGES","변경된 내용이 없습니다."),
    RELOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RELOAD_FAILED","데이터를 불러오는 데 실패했습니다."),

    /**
     *  Weather
     */
    WEATHER_API_FAILURE(HttpStatus.BAD_GATEWAY, "WEATHER_API_FAILURE", "날씨 데이터를 가져오는데 실패했습니다."),
    EMPTY_WEATHER_DATA(HttpStatus.NO_CONTENT, "EMPTY_WEATHER_DATA", "날씨 데이터가 없습니다."),
    TODAY_WEATHER_NOT_FOUND(HttpStatus.NOT_FOUND, "TODAY_WEATHER_NOT_FOUND", "오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.");

    /**
     * Http 상태 코드
     */
    private final HttpStatus status;

    /**
     * 사용자 정의 예외 코드
     */
    private final String code;

    /**
     * 예외 메시지
     */
    private final String message;

}
