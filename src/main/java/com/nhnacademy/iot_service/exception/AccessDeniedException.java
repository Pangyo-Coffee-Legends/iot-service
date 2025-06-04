package com.nhnacademy.iot_service.exception;

/**
 * 접근 권한이 없을 때 발생하는 예외입니다.
 * <p>
 * 인증 또는 인가되지 않은 사용자가 보호된 리소스에 접근하려 할 때 사용합니다.
 * </p>
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * 접근 거부 사유 메시지와 함께 예외를 생성합니다.
     *
     * @param message 접근 거부 사유를 설명하는 메시지
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}