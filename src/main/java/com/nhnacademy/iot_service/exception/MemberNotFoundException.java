package com.nhnacademy.iot_service.exception;

/**
 * 회원 정보를 찾을 수 없을 때 발생하는 예외입니다.
 * <p>
 * 주로 주어진 이메일로 회원을 조회했을 때 결과가 없을 경우 사용합니다.
 * </p>
 */
public class MemberNotFoundException extends RuntimeException {

    /**
     * 주어진 이메일 정보로 회원을 찾을 수 없을 때 예외를 생성합니다.
     *
     * @param message 회원 이메일 등 추가 정보
     */
    public MemberNotFoundException(String message) {
        super("Member not found email : %s".formatted(message));
    }
}