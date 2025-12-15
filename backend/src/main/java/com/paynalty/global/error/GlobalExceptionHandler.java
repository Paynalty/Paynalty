package com.paynalty.global.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     * 비즈니스 로직에서 의도적으로 발생시킨 예외
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    /**
     * @Valid 검증 실패 (RequestBody)
     * DTO 객체의 필드 검증 실패 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .status(GlobalErrorCode.INVALID_INPUT_VALUE.getStatus().value())
                .error(GlobalErrorCode.INVALID_INPUT_VALUE.getStatus().name())
                .code(GlobalErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(errorMessage)
                .build());
    }

    /**
     * @ModelAttribute 검증 실패
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("handleBindException", e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .status(GlobalErrorCode.INVALID_INPUT_VALUE.getStatus().value())
                .error(GlobalErrorCode.INVALID_INPUT_VALUE.getStatus().name())
                .code(GlobalErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(errorMessage)
                .build());
    }

    /**
     * @Validated 파라미터 검증 실패
     * 메서드 파라미터에 대한 제약 조건 위반 시 발생
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("handleConstraintViolationException", e);
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .status(GlobalErrorCode.INVALID_INPUT_VALUE.getStatus().value())
                .error(GlobalErrorCode.INVALID_INPUT_VALUE.getStatus().name())
                .code(GlobalErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(errorMessage)
                .build());
    }

    /**
     * 요청 파라미터 타입 불일치
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        String errorMessage = String.format("Parameter '%s' should be of type %s",
                e.getName(), e.getRequiredType().getSimpleName());

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .status(GlobalErrorCode.INVALID_TYPE_VALUE.getStatus().value())
                .error(GlobalErrorCode.INVALID_TYPE_VALUE.getStatus().name())
                .code(GlobalErrorCode.INVALID_TYPE_VALUE.getCode())
                .message(errorMessage)
                .build());
    }

    /**
     * 필수 요청 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("handleMissingServletRequestParameterException", e);
        String errorMessage = String.format("Required parameter '%s' is missing", e.getParameterName());

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .status(GlobalErrorCode.MISSING_REQUEST_PARAMETER.getStatus().value())
                .error(GlobalErrorCode.MISSING_REQUEST_PARAMETER.getStatus().name())
                .code(GlobalErrorCode.MISSING_REQUEST_PARAMETER.getCode())
                .message(errorMessage)
                .build());
    }

    /**
     * 요청 본문(RequestBody) 형식 오류
     * JSON 파싱 실패 등
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadableException", e);

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .status(GlobalErrorCode.INVALID_REQUEST_BODY.getStatus().value())
                .error(GlobalErrorCode.INVALID_REQUEST_BODY.getStatus().name())
                .code(GlobalErrorCode.INVALID_REQUEST_BODY.getCode())
                .message(GlobalErrorCode.INVALID_REQUEST_BODY.getMessage())
                .build());
    }

    /**
     * 지원하지 않는 HTTP 메서드
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);

        return ResponseEntity.status(GlobalErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ErrorResponse.builder()
                        .status(GlobalErrorCode.METHOD_NOT_ALLOWED.getStatus().value())
                        .error(GlobalErrorCode.METHOD_NOT_ALLOWED.getStatus().name())
                        .code(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode())
                        .message(GlobalErrorCode.METHOD_NOT_ALLOWED.getMessage())
                        .build());
    }

    /**
     * 그 외 모든 예외 처리
     * 민감정보 노출 방지를 위해 상세 메시지는 로그에만 기록
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException throw Exception : {}", e.getMessage(), e);

        return ResponseEntity.internalServerError().body(ErrorResponse.builder()
                .status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .error(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus().name())
                .code(GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .build());
    }
}
